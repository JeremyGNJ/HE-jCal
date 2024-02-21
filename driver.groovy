
ical-Reader-Trim
Keswick-C8
ical-Reader-Trim
« Drivers code  Import 
Spaces
 
4
 
No wrap
HelpDeleteSave
119
    
120
    Date today = new Date()
121
    String todaydate = new SimpleDateFormat("dd-MM-yy").format(today)
122
    log.debug "${today} & ${todaydate}"
123
    
124
//need to re forcast dates prio to sorting
125
    log.debug "${iCalMap.event.size()}"
126
    iCalMap.event = iCalMap.event.values()sort{ a, b -> a.start <=> b.start} //sort the data
127
    log.debug "sorted ${iCalMap.event.size()}"
128
    iCalMap.event = iCalMap.event.unique()
129
    log.debug "filltered ${iCalMap.event.size()}"
130
    /*
131
    iCalMap.event.each{
132
        if (it.start == null) it.start = it.end // not used that i know off
133
        if (it.end == null) it.end = it.start //used some envents didnt have a end date
134
         (t,d,z) = timeHelp(it.start)
135
          fullstart = z
136
​
137
          (t,d,z) = timeHelp(it.end)
138
          fullend = z
139
/*
140
        if (it.repeatFreq){
141
           
142
            if (it.repeatFreq.contains("DAILY")&& !it.repeatFreq.contains("INTERVAL") && it.repeatNum < 0){ //RRULE:FREQ=DAILY;WKST=TU // SEQUENCE:1
143
                log.debug "${fullstart} and ${it.repeatNum} and ${it.repeatFreq} and ${it.repeatNum}"
144
                fullstart = fullstart + (it.repeatNum-1).days //add dd to time string
145
            }
146
            /*
147
            else if (it.repeatFreq.contains("WEEKLY")) { //RRULE:FREQ=WEEKLY;WKST=MO;UNTIL=20210519T000000Z;INTERVAL=1;BYDAY=TU
148
                log.debug "${it.start} and ${it.repeatNum-1*7} and ${it.repeatFreq}"
149
                it.start = it.start + (it.repeatNum-1*7).days //intervall from the string it.repeatFreq.find("INTERVAL=")+1charcter  //add dd to time string
150
            }
151
            else if (it.repeatFreq.contains("monthly")) { // no data yet
152
                it.start = it.start + (it.repeatNum-1).month //add MM to time string
153
            }
154
            else if (it.repeatFreq.contains("yearly")) { // no data yet
155
                it.start = it.start + (it.repeatNum-1).year //add yyyy to time string
156
            }
157
​
158
        }
159
    
160
    }
161
*/   
162
   //iCalMap.event = iCalMap.event.values()sort{ a, b -> a.start <=> b.start} //sort the data
163
//    log.debug iCalMap.event.size()
164
//    iCalMap.event = iCalMap.event.unique()
165
//    log.debug "fileter sorted again ${iCalMap.event.size()}"
166
​
167
    
168
    Integer MaxCount = 0
169
     sendEvent(name: "CharCount", value: "Working")
170
    attrString = "<table class=iCal>"
171
    curWorkingDate = new SimpleDateFormat("yyyyMMdd").format(today)
172
    curTodayCheck = new SimpleDateFormat("yyyyMMdd").format(today)
173
    //sendEvent(name:"debug",value:curWorkingDate)
174
    iCalMap.event.each{
175
        if (MaxCount < state.maxEvt){
176
          // if (it.start == null) it.start = it.end // not used that i know off
177
          //if (it.end == null) it.end = it.start //used some envents didnt have a end date
178
​
179
            (t,d,z,f) = timeHelp(it.start)
180
            datefriendly = f
181
            fullstart = z
182
            datestart = d
183
            timestart = t
184
              
185
            (t,d,z,f) = timeHelp(it.end)
186
            dateendfriendly = f
187
            fullend = z
188
            timeend = t
189
          
