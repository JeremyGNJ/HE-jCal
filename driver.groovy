/*
thankyou TheBearMay
*/
metadata {
    definition (
        name: "jcal-Reader", 
        namespace: "jeremygnj", 
        author: "JeremyG	",
        importUrl:"https://raw.githubusercontent.com/Mark-C-uk/Hubitat/master/ical"
    ) {
        capability "Actuator"
        capability "Sensor"
        capability "Configuration"
        capability "Initialize"

        attribute "tileAttr", "string" 
        attribute "CharCount", "string"
		    attribute "debug", "string" 
    }   
}

import java.text.SimpleDateFormat
import java.util.TimeZone

preferences {
    input("icalink", "string", title: "ical link(s), seperate with a ;")
    input("updatefeq", "number", title: "Polling Rate (minuites)\nDefault:60", default:60)
    input("shLoc", "bool", title: "Show location info?", default:false)
    input("maxEvt", "number", title: "max number of events to show, if you regualy see 'please select an atribute' on dashboad, reduce this number\nDefault:10", default:10)
    input("fontSize","number", title: "Font size adjust", default:10)
    
}
def installed() {
    log.trace "installed()"
    initialize()
}
def updated(){
    sendEvent(name:"tileAttr",value:"Nothing here yet ")
    log.trace "updated() -  "
    initialize()
}

def initialize(){
    if (icalink == null){
        log.warn "${device} - No ical link"
        return
    }
    if (updatefeq == null) updatefeq = 60
    state.updatefeq = updatefeq*60
    if (shLoc == null) shLoc = false
    state.shLoc = shLoc
    if (maxEvt == null) maxEvt = 10
    state.maxEvt = maxEvt
    if (fontSize == null) fontSize = 10
    state.fontSize = fontSize
    
    log.info "${device} initialize - update fequency= ${state.updatefeq} , font= ${state.fontSize}, show location= ${state.shLoc}, max events= ${state.maxEvt}"
    if (icalink != null) runIn(5,getdata)
}

void getdata(){
    log.debug "${device} get data"
//    Map reqParams = [
//            uri: icalink,
//            timeout: 10
//        ]
    HashMap iCalMap = [:] 
    Integer eCount = 0
    iCalMap.put("event",[:])
    try {
        icalinks = icalink.split(";")
        icalinks.each { it ->
            if(it.startsWith(" ")) it = it.replaceFirst(" ","")
            Map reqParams = [
                uri: it,
                timeout: 10
            ]
     
             
        httpGet(reqParams) { resp ->
            if(resp.status == 200) {
                log.debug "rest status${resp.status}"
                wkStr = resp.data
                //iCalMap.put("event",[:])
               // Integer eCount = 0
                wkStr.eachLine{
                    if(!it.startsWith(" ")){
                    List dSplit= it.split(":")
                    if(dSplit.size()>1){
                         if (dSplit[0].trim()=="BEGIN" && dSplit[1].trim()=="VEVENT") {
                            eCount++
                            iCalMap.event.put(eCount.toString(),[:])
                        }
                        if (eCount != 0 && dSplit[1].trim()!=null){
                            if (dSplit[0].trim().contains("DTSTART")) iCalMap.event[eCount.toString()].put("start",dSplit[1].trim())
                            else if (dSplit[0].trim().contains("DTEND")) iCalMap.event[eCount.toString()].put("end",dSplit[1].trim())
                            else if (dSplit[0].trim()=="LOCATION" && state.shLoc) iCalMap.event[eCount.toString()].put("location",dSplit[1].trim())
                            else if (dSplit[0].trim()=="STATUS") iCalMap.event[eCount.toString()].put("status",dSplit[1].trim())     //CONFIRMED or TENTATIVE
                            else if (dSplit[0].trim()=="SUMMARY") iCalMap.event[eCount.toString()].put("summary",dSplit[1].trim())
                            else if (dSplit[0].trim()=="SEQUENCE") iCalMap.event[eCount.toString()].put("repeatNum",dSplit[1].trim())
                            else if (dSplit[0].trim()=="RRULE") iCalMap.event[eCount.toString()].put("repeatFreq",dSplit[1].trim())
                       }
                    }
                    else { // blank - location, attiees etc
                    }
                  }
                }
            } //end 200 resp
            else { // not 200
                log.warn "${device} Response code ${resp.status}"
            }
        } //end http get
    } //end each ical
    } //end try
    catch (e) {
        log.warn "${device} CATCH $e"
    }
    
    
    Date today = new Date()
    String todaydate = new SimpleDateFormat("dd-MM-yy").format(today)
    log.debug "${today} & ${todaydate}"
    
//need to re forcast dates prio to sorting
    log.debug "${iCalMap.event.size()}"
    iCalMap.event = iCalMap.event.values()sort{ a, b -> a.start <=> b.start} //sort the data
    log.debug "sorted ${iCalMap.event.size()}"
    iCalMap.event = iCalMap.event.unique()
    log.debug "filltered ${iCalMap.event.size()}"
    /*
    iCalMap.event.each{
        if (it.start == null) it.start = it.end // not used that i know off
        if (it.end == null) it.end = it.start //used some envents didnt have a end date
         (t,d,z) = timeHelp(it.start)
          fullstart = z

          (t,d,z) = timeHelp(it.end)
          fullend = z
/*
        if (it.repeatFreq){
           
            if (it.repeatFreq.contains("DAILY")&& !it.repeatFreq.contains("INTERVAL") && it.repeatNum < 0){ //RRULE:FREQ=DAILY;WKST=TU // SEQUENCE:1
                log.debug "${fullstart} and ${it.repeatNum} and ${it.repeatFreq} and ${it.repeatNum}"
                fullstart = fullstart + (it.repeatNum-1).days //add dd to time string
            }
            /*
            else if (it.repeatFreq.contains("WEEKLY")) { //RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20210519T000000Z;INTERVAL=1;BYDAY=TU
                log.debug "${it.start} and ${it.repeatNum-1*7} and ${it.repeatFreq}"
                it.start = it.start + (it.repeatNum-1*7).days //intervall from the string it.repeatFreq.find("INTERVAL=")+1charcter  //add dd to time string
            }
            else if (it.repeatFreq.contains("monthly")) { // no data yet
                it.start = it.start + (it.repeatNum-1).month //add MM to time string
            }
            else if (it.repeatFreq.contains("yearly")) { // no data yet
                it.start = it.start + (it.repeatNum-1).year //add yyyy to time string
            }

        }
    
    }
*/   
   //iCalMap.event = iCalMap.event.values()sort{ a, b -> a.start <=> b.start} //sort the data
//    log.debug iCalMap.event.size()
//    iCalMap.event = iCalMap.event.unique()
//    log.debug "fileter sorted again ${iCalMap.event.size()}"

    
    Integer MaxCount = 0
     sendEvent(name: "CharCount", value: "Working")
    attrString = "<table class=iCal>"
    curWorkingDate = new SimpleDateFormat("yyyyMMdd").format(today)
	curTodayCheck = new SimpleDateFormat("yyyyMMdd").format(today)
	//sendEvent(name:"debug",value:curWorkingDate)
    iCalMap.event.each{
		if (MaxCount < state.maxEvt){
          // if (it.start == null) it.start = it.end // not used that i know off
          //if (it.end == null) it.end = it.start //used some envents didnt have a end date

			(t,d,z,f) = timeHelp(it.start)
			datefriendly = f
			fullstart = z
			datestart = d
			timestart = t
	          
			(t,d,z,f) = timeHelp(it.end)
			dateendfriendly = f
			fullend = z
			timeend = t
		  
			 /* (t,d,z,f) = timeHelp(curWorkingDate)
			  datefriendly = f
			  fullstart = z
			  datestart = d
			  timestart = t
			 */
			if (today<=fullstart || today<=fullend) { //and not canciled?
				sendEvent(name: "debug", value: datestart + " zzzz " + curTodayCheck)
				MaxCount = MaxCount +1
				//thestart = SimpleDateFormat("yyyyMMdd").parse(it.start)
				//sendEvent(name: "debug", value: thestart + " zzzz " + curTodayCheck + " yyy " + fullstart + " = " + datestart)
				if (datestart == curTodayCheck){
					sendEvent(name: "tileAttr", value: "test" + MaxCount +" -1" + datestart + " - " + datefriendly)	 
						//today or date              
						//if (todaydate==datestart){ //today events
						//sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 1 - true")
						attrString+="<tr><td class=rT colspan=2>"+"TODAY"+"</td></tr> "
						//attrString+="<div class=rT>TODAY</div>"
				}
				else if (datestart != curWorkingDate)  { // > 7 days
					sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 1 - false")
					attrString+="<tr><td class=rD colspan=2>"+datefriendly+"</td></tr>" //start date
					//attrString+="<div class=rD>"+datefriendly+"</div>" //start date
					curWorkingDate = datestart
				}
				else{}
			    if (it.start.indexOf("T") == -1) {
					sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 2 - true")
					attrString+="<tr><td class=D>All Day</td><td class=cA>"+it.summary+"</td></tr>"
				} //all day event
				else {
					sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 2 - false")
					attrString+="<tr><td class=cD>"+timestart+" - "+timeend+"</td><td class=cA>"+it.summary+"</td></tr>" //time event
				}
				//description          
				//attrString+="<tr><td ${timestart} - ${it.summary}</td></tr>" //description
				//location   
				//if(it.location != null) attrString+="<tr><td style='font-size:${state.fontSize-5}px'>${it.location.replace('\\','')}</td></tr>" //location
            }
		}
	} 
    
    attrString+="</table>"
     sendEvent(name:"tileAttr",value:attrString)
//log.debug"end"
    if(attrString.length() >= 1024) log.warn "To many Char. please reduce max number of events or turn off location = ${attrString.length()}"
    sendEvent(name:"tileAttr",value:attrString)
    sendEvent(name: "CharCount", value: "${attrString.length()} out of 1024 alowed")
    log.info "done get"
    runIn(state.updatefeq,getdata)
}
                    
private timeHelp(data) {
//log.debug "timeHelp data= $data"
    Date zDate
    if (data.contains("Z")) zDate =  toDateTime(data)
    else if (data.contains("T")) zDate = new SimpleDateFormat("yyyyMMdd'T'kkmmss").parse(data)
    else zDate = new SimpleDateFormat("yyyyMMdd").parse(data)
//log.debug "zDate= $zDate"
    String localTime = new SimpleDateFormat("h:mma").format(zDate)
	String dateTrim = new SimpleDateFormat("MM-dd-MM-yy").format(zDate)
	String dateFriendly = new SimpleDateFormat("EEEE, MMMM dd").format(zDate)
	
//log.debug "timeHelp return=$zDate & $localTime & $dateTrim"     
    return [localTime, dateTrim,zDate,dateFriendly]
}
