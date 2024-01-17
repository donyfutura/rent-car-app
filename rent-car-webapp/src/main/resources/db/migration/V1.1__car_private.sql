create table rent_car.car_private
(
    number                 int not null primary key,
    renter_passport_number varchar(255) not null,
    tx_id                  varchar(255) not null,
    policy_id              varchar(255) not null
);
