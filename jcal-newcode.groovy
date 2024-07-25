/*
thankyou TheBearMay  (dont know tho this is but preserving mark's thankyou)
thankyou mark-c-cuk
*/
metadata {
    definition (
        name: "jcal-new", 
        namespace: "jeremygnj", 
        author: "JeremyG",
        importUrl:"https://raw.githubusercontent.com/JeremyGNJ/HE-jCal/main/jcal-code.groovy"
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
import java.util.Date
import java.util.TimeZone
import java.util.Calendar
import java.time.LocalDate

preferences {
    input("icalink", "string", title: "ical link(s)\n(seperate with a ; character")
    input("updatefeq", "number", title: "Polling Rate (minuites)\nDefault:60", default:60)
    input("shLoc", "bool", title: "Show location? (not hooked up yet)", default:false)
    input("shDesc", "bool", title: "Show descripton? (not hooked up yet", default:false)
    input("hidePrivate", "bool", title: "Hide private appointments?", default:false)
    input("maxEvt", "number", title: "max number of events to show. (this will be replaced with auto-char-count\nDefault:10", default:10)
    input("fontSize","number", title: "Font size adjust (is this needed?)", default:10)
    
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
    if (shDesc == null) shDesc = false
    state.shDesc = shDesc
    if (maxEvt == null) maxEvt = 10
    state.maxEvt = maxEvt
    if (fontSize == null) fontSize = 10
    state.fontSize = fontSize
    if (hidePrivate == null) hidePrivate = false
    state.hidePrivate = hidePrivate
    
    log.info "${device} initialize - update fequency= ${state.updatefeq} , font= ${state.fontSize}, show location= ${state.shLoc}, max events= ${state.maxEvt}, hide Private= ${state.hidePrivate})"
    if (icalink != null) runIn(5,getdata)
}

void getdata(){
    log.debug "${device} get data"
    HashMap iCalMap = [:] 
    Integer eCount = 0
    iCalMap.put("event",[:])
    log.debug icalmap
    try {
        icalinks = icalink.split(";")
        def events = []
        icalinks.each { it ->
            if(it.startsWith(" ")) it = it.replaceFirst(" ","")
            Map reqParams = [
                uri: it,
                timeout: 10
            ]
            httpGet(reqParams) { resp ->
                if(resp.status == 200) {
                    log.debug "rest status${resp.status}"
                    icalString = resp.data
                    def lines = icalString.readLines()
                    def currentEvent = [:]
                    lines.each { line ->
                        if (line.startsWith('BEGIN:VEVENT')) {
                            currentEvent = [:]
                        } 
                        else if (line.startsWith('END:VEVENT')) {
                            events << currentEvent
                        }
                        else if(line.startsWith('RRULE')){
                        
 



 
   def dtstart = event['DTSTART'].substring(8, 14)
    def frequency = event['RRULE'].split(';')[0].split('=')[1]
    def count = event['RRULE'].split(';')[1].split('=')[1] as int
    
    def current = new Date.parse("yyyyMMddHHmmss", dtstart)
    while (current.before(startDate)) {
        if (frequency == 'DAILY') {
            current += 1.days
        } else if (frequency == 'WEEKLY') {
            current += 7.days
        } else if (frequency == 'MONTHLY') {
            current = current.plusMonths(1)
        } else if (frequency == 'YEARLY') {
            current = current.plusYears(1)
        }
        count--
        if (count == 0) {
            return null
        }
    }
    return current







                        }

                        else {
                            def parts = line.split(':', 2)
                            currentEvent[parts[0]] = parts[1]
                        }
                    }
                }  //end 200 resp
                else { // not 200
                log.warn "${device} Response code ${resp.status}"
                }
            } //end http get
        } //end each ical
    } //end try
}//end Getdata

def formatevents(event){




}




events.each { event ->
    def nextOccurrence = findNextOccurrence(event, startDate)
    if (nextOccurrence) {
        println("Next occurrence of '${event['SUMMARY']}': ${nextOccurrence.format("yyyyMMdd'T'HHmmss'Z'")}")
    }
}    
                
def PEvents = []

                
                
                
                
                    List dSplit= it.split(":")
                    if(dSplit.size()>1){
                        if (dSplit[0].trim()=="BEGIN" && dSplit[1].trim()=="VEVENT") {
                            eCount++
                            iCalMap.event.put(eCount.toString(),[:])
                        }
                        if (eCount != 0 && dSplit[1].trim()!=null){
                            if (dSplit[0].trim().contains("DTSTART")){
				                if (dSplit[0].contains("(UTC")){
			    		            //iCalMap.event[eCount.toString()].put("start",dSplit[1].trim())
    					            iCalMap.event[eCount.toString()].put("start", "${dSplit[0].trim()} ${dSplit[1].trim()}")
				                }
				                else {
					                iCalMap.event[eCount.toString()].put("start",dSplit[1].trim())
				                }
                            }
                            else if (dSplit[0].trim().contains("DTEND")) iCalMap.event[eCount.toString()].put("end",dSplit[1].trim())
                            else if (dSplit[0].trim()=="LOCATION" && state. shLoc) iCalMap.event[eCount.toString()].put("location",dSplit[1].trim())
                            else if (dSplit[0].trim()=="STATUS") iCalMap.event[eCount.toString()].put("status",dSplit[1].trim())     //CONFIRMED or TENTATIVE
                            else if (dSplit[0].trim()=="SUMMARY") iCalMap.event[eCount.toString()].put("summary",dSplit[1].trim())
                            else if (dSplit[0].trim()=="SEQUENCE") iCalMap.event[eCount.toString()].put("repeatNum",dSplit[1].trim())
                            else if (dSplit[0].trim()=="RRULE") iCalMap.event[eCount.toString()].put("repeatFreq",dSplit[1].trim())

    
    
    Date today = new Date()
    String todaydate = new SimpleDateFormat("dd-MM-yy").format(today)
    log.debug "${today} & ${todaydate}"
    
    //Sort Events by date
    log.debug "${iCalMap.event.size()}"
    iCalMap.event = iCalMap.event.values()sort{ a, b -> a.start <=> b.start} //sort the data
    //from chatgpt
    //iCalMap.event = iCalMap.event.collectEntries { key, value -> [key, value.sort { a, b -> a.start <=> b.start }] }
    log.debug "sorted ${iCalMap.event.size()}"
    log.debug "itmes ---- ${iCalMap.event.summary} --- ${iCalMap.event.start}"
    //iCalMap.event = iCalMap.event.unique()
    //log.debug "filltered ${iCalMap.event.size()}"

    Integer MaxCount = 0
    sendEvent(name: "CharCount", value: "Working")
    attrString = "<table class=iCal>"
    curWorkingDate = new SimpleDateFormat("yyyyMMdd").format(today)
	curTodayCheck = new SimpleDateFormat("yyyyMMdd").format(today)
    curTomorrowCheck = new SimpleDateFormat("yyyyMMdd").format(today.plus(1))
    iCalMap.event.each{
		if (MaxCount < state.maxEvt){
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
			
            if (today<=fullstart || today<=fullend) { 
				if (it.summary == "Private Appointment" && hidePrivate == true){
                    //skipping
                    log.debug "skipping private appointment"
                }
                    else {
                    sendEvent(name: "debug", value: datestart + " zzzz " + curTodayCheck)
                    MaxCount = MaxCount +1
                    //thestart = SimpleDateFormat("yyyyMMdd").parse(it.start)
                    //sendEvent(name: "debug", value: thestart + " zzzz " + curTodayCheck + " yyy " + fullstart + " = " + datestart)
                    log.debug "Checking " + datefriendly + " " + datestart  + " " + fullstart
                    dayCheck = new SimpleDateFormat("yyyyMMdd").format(fullstart)
                    
                    if (dayCheck == curTodayCheck){
                        sendEvent(name: "tileAttr", value: "test" + MaxCount +" -1" + datestart + " - " + datefriendly)	 
                        attrString+="<tr><td class=rJ colspan=2>"+"TODAY"+"</td></tr>" //start date
                    }
                    else if (dayCheck == curTomorrowCheck)  { 
                        sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 1 - false")
                        attrString+="<tr><td class=rW colspan=2>"+"TOMORROW"+"</td></tr>" //start date
                    }
                    else if (datestart != curWorkingDate)  { // > 7 days
                        sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 1 - false")
                        attrString+="<tr><td class=rD colspan=2>"+datefriendly+"</td></tr>" //start date
                        curWorkingDate = datestart
                    }
                    else{}
                    if (it.start.indexOf("T") == -1) {
                        sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 2 - true")
                        attrString+="<tr><td class=cD>All Day</td><td class=cA>"+it.summary+"</td></tr>"
                    } //all day event
                    else {
                        sendEvent(name: "tileAttr", value: "test" + MaxCount +" - 2 - false")
                        attrString+="<tr><td class=cD>"+timestart+" - "+timeend+"</td><td class=cA>"+it.summary+"</td></tr>" //time event
                    }
                }
            }
		}
	} 
    
    attrString+="</table>"
     sendEvent(name:"tileAttr",value:attrString)
    log.debug"end"
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


