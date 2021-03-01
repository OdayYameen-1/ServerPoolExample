*** Settings ***
Library    RequestsLibrary 


*** Variables ***
${HOST}    http://localhost:8080


*** Test Cases ***
GetServers
    Create Session    SessName    ${HOST}
    ${headers}=    Create Dictionary    Accept=*/*    Content-Type=application/json    charset=utf-8

	# GET Request
	${response}=    Get On Session    SessName    getServer    headers=${headers}
	Log    ${response.status_code}     
	Log To Console    ${response.content}    