## Diagramas

### <li>Clases

````plantuml
@startuml
' --------------------------------------------------------
' Styles
' --------------------------------------------------------
left to right direction
skinparam linetype ortho
skinparam backgroundColor #f45958
skinparam dpi 150
skinparam nodesep 200        ' espacio mínimo entre nodos
skinparam ranksep 200        ' espacio mínimo entre niveles verticales
skinparam edgesep 150        ' espacio mínimo entre líneas que se cruzan
skinparam packageMargin 30   ' padding interno de cada paquete

skinparam class {
    BackgroundColor #F4F4F4
    BorderColor #555555
    FontName Arial
    FontSize 12
    FontColor #333333
    AttributeFontColor #333333
    Padding 20              ' espacio entre contenido y borde
}

skinparam Arrow {
    Color #555555
    Thickness 1
    FontSize 12
}

' --------------------------------------------------------
' Diagram
' --------------------------------------------------------
package "Users & Security" as US {
    class Account {
        + Long id
        + String username
        + String email
        + String password
        + Boolean active
        + Set<Role> roles
    }
    class Role
    class AppUser {
        + Long id
    }

    Account --> Role     : 1 .. *
    Account -- AppUser  : 1 .. 1
}

package "Auctionable Entities" as AE {
    abstract class AuctionableEntity {
        + Long id
        + Long price
        + Long points
        + Long previousPoints
        + Boolean active
        + String nationality
        + String imageUrl
    }
    class Driver {
        + String name
    }
    class Team {
        + String name
    }

    Driver -- AuctionableEntity : 1 .. 1
    Team   -- AuctionableEntity : 1 .. 1
}

package "League & Market" as LM {
    class League {
        + Long id
        + String name
    }
    class Market {
        + Long id
    }
    class MarketItem {
        + Long id
        + Boolean available
    }
    class Offer {
        + Long id
        + Boolean buy
        + Long price
        + LocalDateTime timestamp
    }

    League            -- Market         : 1 .. 1
    League            --> MarketItem     : 1 .. *
    Market            --> MarketItem     : 1 .. *
    AuctionableEntity --> MarketItem     : 1 .. *
    AppUser           --> Offer          : 1 .. *
    MarketItem        --> Offer          : 1 .. *
}

package "LineUp & Budget" as LB {
    class LineUp {
        + Long id
        + Long totalPoints
    }
    class Budget {
        + Long id
        + Long amount
    }

    AppUser --> LineUp   : 1 .. *
    League  --> LineUp   : 1 .. *
    Team    --> LineUp   : 1 .. *
    Driver  --> LineUp   : 1 .. *

    AppUser --> Budget   : 1 .. *
    League  --> Budget   : 1 .. *
}

US -[hidden]-> AE
AE -[hidden]-> LM
LM -[hidden]-> LB

@enduml
````

### <li> E-R

````plantuml
@startuml
' --------------------------------------------------------
' Styles
' --------------------------------------------------------
left to right direction
skinparam linetype ortho
skinparam backgroundColor #f45958
skinparam dpi 150
skinparam nodesep 200
skinparam ranksep 200
skinparam edgesep 150

skinparam entity {
    BackgroundColor #F4F4F4
    BorderColor #555555
    FontName Arial
    FontSize 12
    FontColor #333333
    AttributeFontColor #333333
    Padding 20
}

skinparam Arrow {
    Color #555555
    Thickness 1
    FontSize 12
}

' --------------------------------------------------------
' Entities
' --------------------------------------------------------
entity "ACCOUNT" as ACCOUNT {
  * id : LONG
  --
  username : VARCHAR
  email    : VARCHAR
  password : VARCHAR
  active   : BOOLEAN
}

entity "ROLE" as ROLE {
  * name : VARCHAR
}

entity "APP_USER" as APP_USER {
  * id : LONG
}

entity "AUCTIONABLE_ENTITY" as AE {
  * id             : LONG
  --
  price           : BIGINT
  points          : BIGINT
  previous_points : BIGINT
  active          : BOOLEAN
  nationality     : VARCHAR
  image_url       : VARCHAR
  type            : VARCHAR
}

entity "DRIVER" as DRIVER {
  * id   : LONG
  --
  name : VARCHAR
}

entity "TEAM" as TEAM {
  * id   : LONG
  --
  name : VARCHAR
}

entity "LEAGUE" as LEAGUE {
  * id   : LONG
  --
  name : VARCHAR
}

entity "MARKET" as MARKET {
  * id : LONG
}

entity "MARKET_ITEM" as MARKET_ITEM {
  * id        : LONG
  --
  available  : BOOLEAN
}

entity "OFFER" as OFFER {
  * id        : LONG
  --
  buy        : BOOLEAN
  price      : BIGINT
  timestamp  : TIMESTAMP
}

entity "LINE_UP" as LINE_UP {
  * id           : LONG
  --
  total_points  : BIGINT
}

entity "BUDGET" as BUDGET {
  * id     : LONG
  --
  amount : BIGINT
}

' --------------------------------------------------------
' 1:N y 1:1
' --------------------------------------------------------
ACCOUNT    -- APP_USER    : 1 : 1
APP_USER   --> LINE_UP     : 1 : N
APP_USER   --> BUDGET      : 1 : N
APP_USER   --> OFFER       : 1 : N
LEAGUE     --> LINE_UP     : 1 : N
LEAGUE     --> BUDGET      : 1 : N
LEAGUE     -- MARKET      : 1 : 1
OFFER      <-- MARKET_ITEM : N : 1
LINE_UP    <-- TEAM        : N : 1
MARKET_ITEM <-- AE         : N : 1

' --------------------------------------------------------
' N:M
' --------------------------------------------------------
ACCOUNT    <--> ROLE        : N:M
LINE_UP    <--> DRIVER      : N:M
MARKET     <--> MARKET_ITEM : N:M

' --------------------------------------------------------
' Inheritance
' --------------------------------------------------------
AE         <|-- DRIVER
AE         <|-- TEAM

ACCOUNT -[hidden]-> AE
AE      -[hidden]-> LEAGUE
LEAGUE  -[hidden]-> LINE_UP

@enduml
````

### <li> Use Case

````plantuml
@startuml
' --------------------------------------------------------
' Styles
' --------------------------------------------------------
left to right direction
skinparam dpi 120
skinparam backgroundColor #f45958
scale 0.5

skinparam actor {
    BackgroundColor #EFEFEF
    BorderColor #555555
    FontName Arial
    FontSize 12
    FontColor #333333
}

skinparam usecase {
    BackgroundColor #F4F4F4
    BorderColor #555555
    FontName Arial
    FontSize 12
    FontColor #333333
    HorizontalSeparation 60
    VerticalSeparation 40
}

actor Admin
actor Maintainer
actor Player
actor System

' --------------------------------------------------------
' ADMIN
' --------------------------------------------------------
usecase "Create User"    as CU1
usecase "Read User"      as RU1
usecase "Update User"    as UU1
usecase "Delete User"    as DU1
Admin --> CU1
Admin --> RU1
Admin --> UU1
Admin --> DU1


usecase "Create Team"    as CT1
usecase "Read Team"      as RT1
usecase "Update Team"    as UT1
usecase "Delete Team"    as DT1
Admin --> CT1
Admin --> RT1
Admin --> UT1
Admin --> DT1


usecase "Create Driver"  as CD1
usecase "Read Driver"    as RD1
usecase "Update Driver"  as UD1
usecase "Delete Driver"  as DD1
Admin --> CD1
Admin --> RD1
Admin --> UD1
Admin --> DD1

' --------------------------------------------------------
' MANTAINER
' --------------------------------------------------------
usecase "Assign Points to Teams"    as UC4
usecase "Assign Points to Drivers"  as UC5
Maintainer --> UC4
Maintainer --> UC5

' --------------------------------------------------------
' PLAYER
' --------------------------------------------------------
usecase "Create League"                   as UC6
usecase "Join League"                     as UC7
usecase "Modify Lineup"                   as UC8
usecase "Make Offer"                      as UC9
usecase "View Market"                     as UC10
usecase "View GrandPrix MVPs"             as UC11
usecase "View League Rankings"            as UC12
usecase "Display LineUp Item in Market"   as UC13

Player --> UC6
Player --> UC7
Player --> UC8
Player --> UC9
Player --> UC10
Player --> UC11
Player --> UC12
Player --> UC13

UC6 ..> UC7 : «include»


usecase "Process Player Offers"      as UC16

usecase "Select Highest Offers"      as UC19
usecase "Make LineUp Item Offer"     as UC20
UC16 ..> UC19 : «include»
UC16 ..> UC20 : «include»


UC13 ..> UC20 : «include»

' --------------------------------------------------------
' SYSTEM
' --------------------------------------------------------
usecase "Rotate Market Items"        as UC14
usecase "Calculate Market Values"    as UC15
usecase "Copy DB to Secondary DB"    as UC17
usecase "Update Lineup Total Points" as UC18

System --> UC14
System --> UC15
System --> UC16
System --> UC17
System --> UC18


UC9 ..> UC20 : «extend»

@enduml
````