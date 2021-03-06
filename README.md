## How to deploy

### Locally
- harbor

### At ec2
- If the mysql isnt running:

```bash
docker run --name mysql -e MYSQL_DATABASE=checkout -e MYSQL_ROOT_PASSWORD=<password> -v /mysql:/var/lib/mysql -d mysql/mysql-server --character-set-server=utf8 --collation-server=utf8_general_ci

```

- Pull the new version 

```bash
docker pull leocwolter/checkout
```

- Stop and remove the running container

```
docker stop checkout
docker rm checkout
```

- Run the container:

```bash
docker run --name checkout -p 8080:8080 --link mysql:mysql -d leocwolter/checkout
```

## How to setup locally

- Import the project as an Existing Maven Project at eclipse
- Import base.sql into your checkout database:

```bash
mysql -u root checkout < base.sql
``` 

- Copy the application.properties.sample:

```bash
cp src/main/resources/application.properties.sample src/main/resources/application.properties
```

- Set the amazon properties


## How to run

- If you have the project imported, just run the class com.senzo.qettal.checkout.CheckoutApplication
- If you don't, run 'mvn spring-boot:run' at your terminal

## How to test

 If you don't have an application to test with, use curl sending a json with the structure provided bellow:

## Login

See: https://github.com/tcsenzo/authentication/blob/master/README.md 

**All endpoints marked with (REQUIRES LOGIN) bellow will return 401 if you are not logged in**

## Purchases

### How to create one (REQUIRES LOGIN)

- Json template:

```json
{
	"event_id": 1,
	"items": [
		{
			"quantity": 2,
			"ticket_type": "HALF" 
		},
		{
			"quantity": 3,
			"ticket_type": "FULL"
		}
	]
}
```

Example:

```bash
curl -b /tmp/cookies.txt -H "Content-Type:application/json" -X POST http://localhost:8082/purchases --data "{\"event_id\": 1, \"items\": [{ \"quantity\": 2, \"ticket_type\": \"HALF\"}, { \"quantity\": 3, \"ticket_type\": \"FULL\"}]}"
```

Possible responses:

- 202 - Purchase created
- 400 - Invalid or insufficient data
- 409 - Event not available

The success json will be the following:
```json
{
	"items": [{
		"quantity": 2,
		"ticket_type": "HALF",
		"unit_price": 11.0,
		"total_price": 22.0
	}, {
		"quantity": 3,
		"ticket_type": "FULL",
		"unit_price": 22.0,
		"total_price": 66.0
	}],
	"totalQuantity": 5,
	"id": 2
}
```

## Payments

### How to create one (REQUIRES LOGIN)

- Json template:

```json
{
	"credit_card_hash": "GMgxgzN3gg+wCGliLgKFR/fUaAPZH8sq/NJlZkF3D69xL0uUKsak4KLGDNms+6QG9Oc7PMh5J4FD53tna8Xr9bLotrVdcle9Gr+ORl/qdx3DraW8YP4k+aGiSOHD250rm4LVdkSMT0za8JAUEbINy6mpgORDsMXLwUJs4ExdwI4WDbMow8gk1p0yWx2ldVBuNZVC+PtuLWulE+zg56X0crs5IaEPfg2XucSNBQEy5GeMPZcZ/meJO4G+KfvZ0pMnxcV0Dmx2CXxi9qLRFlJrmoSFkqeqVFNZbmtQhqdAmvRGOqJX+d8nzhWepOiT3JBkSmkAgLpQeYDGu5MhgI2AXg==",
	"full_name": "Leonardo Cesar Wolter",
	"birth_date": "1994-07-18",
	"phone_area_code": "11",
	"phone": "99999999",
	"cpf": "111.111.111-11",
	"purchase_id": "1"
}
```

Example:

```bash
curl -b /tmp/cookies.txt -H "Content-Type:application/json" -X POST http://localhost:8082/payments --data "{\"credit_card_hash\":\"GMgxgzN3gg+wCGliLgKFR/fUaAPZH8sq/NJlZkF3D69xL0uUKsak4KLGDNms+6QG9Oc7PMh5J4FD53tna8Xr9bLotrVdcle9Gr+ORl/qdx3DraW8YP4k+aGiSOHD250rm4LVdkSMT0za8JAUEbINy6mpgORDsMXLwUJs4ExdwI4WDbMow8gk1p0yWx2ldVBuNZVC+PtuLWulE+zg56X0crs5IaEPfg2XucSNBQEy5GeMPZcZ/meJO4G+KfvZ0pMnxcV0Dmx2CXxi9qLRFlJrmoSFkqeqVFNZbmtQhqdAmvRGOqJX+d8nzhWepOiT3JBkSmkAgLpQeYDGu5MhgI2AXg==\",\"full_name\":\"Leonardo Cesar Wolter\",\"birth_date\":\"1994-07-18\",\"phone_area_code\":\"11\",\"phone\":\"99999999\",\"cpf\":\"111.111.111-11\",\"purchase_id\":\"1\"}" -i
```

Possible responses:

- 202 - Payment created
- 400 - Invalid or insufficient data
- 404 - Purchase not found


### How to simulate moip callbacks

Setting payment status to APPROVED:

Example

```bash
curl -X POST http://localhost:8082/payments/moip/callback -d id_transacao=<purchase_unique_id> -d status_pagamento=1
```

Possible responses:

- 200 - Ok
- 404 - Purchase or payment not found


## Tickets

### How to create one

You have to send the moip callback above to confirm your payment.
If you do this, your ticket will be generated automatically

### How to get a specific event (REQUIRES LOGIN)


Example

```
curl -b /tmp/cookies.txt -H "Content-Type:application/json" http://localhost:8082/tickets/0d63f1d98c1d0e718a058ca57add34555e15a56dadbee5c1d4eb6ada69c70035
```

The response will be a json in as the one bellow:

```json
{
    "event": {
        "name": "Evento maroto",
        "price": 22.0,
        "scheduled_date": "2017-12-03T10:15:30Z",
        "theater": {
            "address": {
                "city": "S\u00e3o Paulo",
                "country": "Brasil",
                "district": "Vila Olimpia",
                "number": "360",
                "state": "SP",
                "street": "Rua Olimp\u00edadas",
                "zip_code": "04551-000"
            },
            "name": "Teatro NET SP"
        }
    },
    "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/0d63f1d98c1d0e718a058ca57add34555e15a56dadbee5c1d4eb6ada69c70035.png",
    "user": {
        "name": "Leonardo"
    }
}
```

## History

### How to get your previous purchases made by you (REQUIRES LOGIN)


Example

```
curl -b /tmp/cookies.txt http://localhost:8082/history
```

The response will be a json in as the one bellow:

```json
[
    {
        "date": "2016-08-20T22:48:03",
        "event": {
            "name": "Evento maroto",
            "scheduled_date": "2017-12-03T10:15:30Z",
            "theater": {
                "address": {
                    "city": "S\u00e3o Paulo",
                    "country": "Brasil",
                    "district": "Vila Olimpia",
                    "number": "360",
                    "state": "SP",
                    "street": "Rua Olimp\u00edadas",
                    "zip_code": "04551-000"
                },
                "name": "Teatro NET SP"
            }
        },
        "id": 2,
        "tickets": []
    },
    {
        "date": "2016-08-20T20:52:19",
        "event": {
            "name": "Evento maroto",
            "scheduled_date": "2017-12-03T10:15:30Z",
            "theater": {
                "address": {
                    "city": "S\u00e3o Paulo",
                    "country": "Brasil",
                    "district": "Vila Olimpia",
                    "number": "360",
                    "state": "SP",
                    "street": "Rua Olimp\u00edadas",
                    "zip_code": "04551-000"
                },
                "name": "Teatro NET SP"
            }
        },
        "id": 1,
        "payment_status": "APPROVED",
        "tickets": [
            {
                "paid_price": 11.0,
                "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ac01898a7648c27eb849708fb8dc03e184b971f698bc639e3414e19607226590.png",
                "type": "HALF"
            },
            {
                "paid_price": 11.0,
                "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/3c1dfae608e1cc545d74b352845253d8efc4bbbce4f2b0a8a6f91062dde81559.png",
                "type": "HALF"
            },
            {
                "paid_price": 22.0,
                "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/e9aaebe256431708c6a9c9f4c3f1bb3d1652a8671a27aaa284d395c3e3039e64.png",
                "type": "FULL"
            },
            {
                "paid_price": 22.0,
                "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/d93059227c00e205b3f5bb4eb8b1f9d760fd7d17b57ab710515f9d9855026b65.png",
                "type": "FULL"
            },
            {
                "paid_price": 22.0,
                "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ef110b8af3eed078cc15fed3da8e5e1536f58150f33977c5a6471b463f4333a9.png",
                "type": "FULL"
            }
        ]
    }
]

```

### How to get details of a specific purchase (REQUIRES LOGIN)

Example

```bash
curl -b /tmp/cookies.txt http://localhost:8082/history/1
```

Possible responses:

- 200 - OK
- 404 - Purchase not found
- 403 - You don't have sufficient permission to see this purchase's information

The success json will be as the one bellow:


```json
{
    "date": "2016-08-20T20:52:19",
    "event": {
        "name": "Evento maroto",
        "scheduled_date": "2017-12-03T10:15:30Z",
        "theater": {
            "address": {
                "city": "S\u00e3o Paulo",
                "country": "Brasil",
                "district": "Vila Olimpia",
                "number": "360",
                "state": "SP",
                "street": "Rua Olimp\u00edadas",
                "zip_code": "04551-000"
            },
            "name": "Teatro NET SP"
        }
    },
    "id": 1,
    "payment_status": "APPROVED",
    "tickets": [
        {
            "paid_price": 11.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ac01898a7648c27eb849708fb8dc03e184b971f698bc639e3414e19607226590.png",
            "type": "HALF"
        },
        {
            "paid_price": 11.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/3c1dfae608e1cc545d74b352845253d8efc4bbbce4f2b0a8a6f91062dde81559.png",
            "type": "HALF"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/e9aaebe256431708c6a9c9f4c3f1bb3d1652a8671a27aaa284d395c3e3039e64.png",
            "type": "FULL"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/d93059227c00e205b3f5bb4eb8b1f9d760fd7d17b57ab710515f9d9855026b65.png",
            "type": "FULL"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ef110b8af3eed078cc15fed3da8e5e1536f58150f33977c5a6471b463f4333a9.png",
            "type": "FULL"
        }
    ]
}
```

### How to get purchases of a specific theater (REQUIRES LOGIN)
(doesn't include the tickets information)


Example

```bash
curl -b /tmp/cookies.txt http://localhost:8082/theaters/1/history
```

The response will be a json in as the one bellow:

```json
[
    {
        "date": "2016-09-04T19:17:39",
        "event": {
            "name": "Evento maroto",
            "scheduled_date": "2017-12-03T10:15:30Z",
            "theater": {
                "address": {
                    "city": "S\u00e3o Paulo",
                    "country": "Brasil",
                    "district": "Vila Olimpia",
                    "number": "360",
                    "state": "SP",
                    "street": "Rua Olimp\u00edadas",
                    "zip_code": "04551-000"
                },
                "name": "Teatro NET SP"
            }
        },
        "id": 2,
        "items": [
            {
                "quantity": 2,
                "ticket_type": "HALF",
                "total_price": 22.0,
                "unit_price": 11.0
            },
            {
                "quantity": 3,
                "ticket_type": "FULL",
                "total_price": 66.0,
                "unit_price": 22.0
            }
        ]
    }
]


```

### How to get details of a specific purchase of a specific theater (REQUIRES LOGIN)
(doesn't include the tickets information)

Example

```bash
curl -b /tmp/cookies.txt http://localhost:8082/theater/1/history/1
```

Possible responses:

- 200 - OK
- 404 - Purchase not found for the specified theater

The success json will be as the one bellow:


```json
{
    "date": "2016-08-20T20:52:19",
    "event": {
        "name": "Evento maroto",
        "scheduled_date": "2017-12-03T10:15:30Z",
        "theater": {
            "address": {
                "city": "S\u00e3o Paulo",
                "country": "Brasil",
                "district": "Vila Olimpia",
                "number": "360",
                "state": "SP",
                "street": "Rua Olimp\u00edadas",
                "zip_code": "04551-000"
            },
            "name": "Teatro NET SP"
        }
    },
    "id": 1,
    "payment_status": "APPROVED",
    "tickets": [
        {
            "paid_price": 11.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ac01898a7648c27eb849708fb8dc03e184b971f698bc639e3414e19607226590.png",
            "type": "HALF"
        },
        {
            "paid_price": 11.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/3c1dfae608e1cc545d74b352845253d8efc4bbbce4f2b0a8a6f91062dde81559.png",
            "type": "HALF"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/e9aaebe256431708c6a9c9f4c3f1bb3d1652a8671a27aaa284d395c3e3039e64.png",
            "type": "FULL"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/d93059227c00e205b3f5bb4eb8b1f9d760fd7d17b57ab710515f9d9855026b65.png",
            "type": "FULL"
        },
        {
            "paid_price": 22.0,
            "qrcode_url": "http://dev.qettal.tickets.s3.amazonaws.com/ef110b8af3eed078cc15fed3da8e5e1536f58150f33977c5a6471b463f4333a9.png",
            "type": "FULL"
        }
    ]
}
```