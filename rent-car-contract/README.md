# rent-car-contract

## Методы и примеры транзакций для их вызова

### `initRent()`
Создает контракт на ноде и инициализирует стейт с несколькими авто и по ключу CONTRACT_CREATOR кладет адерес отправителя текущей транзакции.  
Пример транзакции:
```json
{
    "image": "image",
    "fee": 0,
    "imageHash": "imageHash",
    "type": 103,
    "params": [
        {
            "type": "string",
            "value": "initRent",
            "key": "action"
        }
    ],
    "version": 2,
    "sender": "sender",
    "password": "sender",
    "contractName": "rent-car-contract"
}
```

### `setBlackListContract(contractId: String)`
Добавлет на стейт по ключу `BLACK_LIST_CONTRACT_ID` переданное значение (контракт необходим для проверок renter в черном списке).  
Пример транзакции:
```json
{
    "contractId": "contractId",
    "fee": 0,
    "sender": "sender",
    "password": "password",
    "type": 104,
    "params":
    [
        {
           "type": "string",
           "key": "action",
           "value": "setBlackListContract"
        },
        {
           "type": "string",
           "key": "contractId",
           "value": "black_list_contract_id"

        }
    ],
    "version": 2,
    "contractVersion": 1
}
```

### `createCar(car: Car)`
Добавлет в стейт через маппинг cars авто. Также, проверяет, что адрес отправителя транзакции равен адресу создателю контракта.  
Пример транзакции:
```json
{
    "contractId": "contractId",
    "fee": 0,
    "sender": "sender",
    "password": "password",
    "type": 104,
    "params":
    [
        {
           "type": "string",
           "key": "action",
           "value": "createCar"
        },
        {
           "type": "string",
           "key": "car",
           "value": "{\"name\":\"volvo\",\"renter\":null,\"number\":\"4\"}"

        }
    ],
    "version": 2,
    "contractVersion": 1
}
```

### `changeCarRenter(carNumber: String, carRenter: String)`
Меняет renter для авто по его номеру (Доступно только создателю контракта). Также, проверяет, есть ли авто с таким номером на стейте.  
Пример транзакции:
```json
{
    "contractId": "contractId",
    "fee": 0,
    "sender": "sender",
    "password": "password",
    "type": 104,
    "params":
    [
        {
           "type": "string",
           "key": "action",
           "value": "changeCarRenter"
        },
        {
           "type": "string",
           "key": "carNumber",
           "value": "1"
        },
        {
           "type": "string",
           "key": "carRenter",
           "value": "3NqNVU8XpEWLR86zvGAyZ6QL4xSse1EDb7K"
        }
    ],
    "version": 2,
    "contractVersion": 1
}
```

### `rentCar(carNumber: String)`
Аренда авто любым пользователем (отправителем тарнзакции).  
Если установлен контракт черного списка, то проверяет отправителя на нахождение в нем (если находит транзакция не выполняется).  
Проверяет есть ли на стейте авто с переданным в параметр номером (Если такого авто нет, транзакция не выполяется).  
Проверяет не занята ли машина другим renter (Если занята, тарнзакция не выполняется).  
Если все проверки успешно проходят - меняет у авто по переданному номеру поле renter у объекта Car на стейте.  
Пример транзакции:
```json
{
    "contractId": "contractId",
    "fee": 0,
    "sender": "sender",
    "password": "password",
    "type": 104,
    "params":
    [
        {
           "type": "string",
           "key": "action",
           "value": "rentCar"
        },
        {
           "type": "string",
           "key": "carNumber",
           "value": "3"

        }
    ],
    "version": 2,
    "contractVersion": 1
}
```