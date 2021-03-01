*** Settings ***
Library    RequestsLibrary 


*** Variables ***
${HOST}    http://localhost:8080


*** Test Cases ***
AllocateServerLessThan100
	 Create Session    allocate    ${HOST}
	
     ${headers}=    Create Dictionary    Accept=*/*    Content-Type=application/json    charset=utf-8

	# GET Request
	${response}=    Get On Session    allocate    allocate/30/oday    headers=${headers}
	Get On Session    allocate    allocate/100/oday    headers=${headers}
	
	Log    ${response.status_code}     
	Log To Console    ${response.content}    

AllocateServerGraterThan100
    Create Session    allocate    ${HOST}

      ${headers}=    Create Dictionary    Accept=*/*    Content-Type=application/json    charset=utf-8
	# GET Request
	  ${response}=    Get On Session    allocate    allocate/450/oday    headers=${headers}
	   #Get On Session    allocate    allocateG/450/ahmad    headers=${headers}
	
	  Log    ${response.status_code}     
	  Log To Console    ${response.content}   