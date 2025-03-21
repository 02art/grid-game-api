## 5x5 Grid Game API
### made with: sequencediagram.org

![](/Users/art/Documents/Coding Stuff/grid-game-api/src/main/resources/img/seqDiag.svg)


## SEQUENCE DIAGRAM "CODE" ##

````
title 5x5 Grid Game API - Sequence Diagram

actor Client
participant Server

Client->Server: POST /game/start
Server-->Client: 200 OK (gameID, position="A1")

loop Bewegung
    Client->Server: POST /game/move {sessionId, direction}
    alt Ziel erreicht (E5)
        Server-->Client: 200 OK (position="E5", status="success")
    else Bewegung möglich
        Server-->Client: 200 OK (position, status="ongoing")
    else Blockade
        Server-->Client: 400 Bad Request (position, status="blocked")
    end
end
````


