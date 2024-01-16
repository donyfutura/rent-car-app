# black-list-contract

Создание контракта на ноде осуществляется отправкой 103-ей транзакции:
```json
{
    "image": "image",
    "fee": 0,
    "imageHash": "imageHash",
    "type": 103,
    "params": [
        {
            "type": "string",
            "value": "init",
            "key": "action"
        }
    ],
    "version": 2,
    "sender": "sender",
    "password": "password",
    "feeAssetId": null,
    "contractName": "black-list-contract"
}
```

Добавление адреса в черный список осуществляется с помощью отправки 104-ой транзакции:
```json
{
    "contractId": "contractId",
    "fee": 0,
    "sender": "sender",
    "password": "sender",
    "type": 104,
    "params":
    [
        {
           "type": "string",
           "key": "action",
           "value": "addRenter"
        },
        {
           "type": "string",
           "key": "address",
           "value": "some_address"
        }
    ],
    "version": 2,
    "contractVersion": 1
}
```
