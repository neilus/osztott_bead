*** Settings ***
Documentation  Egyszerű beadandó tesztelése
Library        Telnet
Library        String
Library        DateTime
Library        Collections
Library        Process

Suite Setup      Startup Server
Suite Teardown   Close Connections And Terminate The Server
*** Variables ***
${HOST}     127.0.0.1
${PORT}     65456

*** Test Cases ***
Check The Server
    Is Process Running      ${server}
#    Wait For Process    on_timeout=continue
Mario Logs In
    Log To Console      Mario connects to ${HOST} ${PORT}
    CONNECT WITH 1ST CLIENT

#Wait For Players
#    [Documentation]  Waits for 2 clients to join the server
#    ...              The Clients send their names when joining to the server
#    ...              After the 2nd client joins it should send
#    ...              a start message to the 1st one
#Create Log File
#    [Documentation]  The server creates a file where it will log the words sent by
#    ...              the clients.
#    ...              The filename should look like:
#    ...              <playername1>_<playername2>_<timestamp>.txt
#Waits For A Single Word
#    [Documentation]  The server waits for a single word from each of the clients at a time

*** Keywords ***
Startup Server
    [Documentation]  Startup server application from bead.egyszeru package
    ${server}=   Start Process    java    -classpath    build/classes/main    bead.egyszeru.JatekSzerver
    ...         alias=JatekSzerver
    Set Suite Variable  ${server}

Close Connections And Terminate The Server
    Terminate All Processes  kill=True
    Close All Connections

Connect With 1st Client
    ${player1}=     Open Connection     ${HOST}     alias=Player1    port=${PORT}
    Write   Mario

Connect With 2nd Client
    ${player2}=     Open Connection     ${HOST}     alias=Player2    port=${PORT}
    Write   Luigi