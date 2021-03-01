*** Settings ***
Library    RequestsLibrary 


*** Variables ***
${HOST}    http://localhost:8080


*** Test Cases ***
SizeOfServerPool
	 Create Session    allocate    ${HOST}
	
     ${headers}=    Create Dictionary    Accept=*/*    Content-Type=application/json    charset=utf-8

	# GET Request
	${response}=    Get On Session    allocate    size    headers=${headers}
	
	
	Log    ${response.status_code}     
	Log To Console    ${response.content}    
