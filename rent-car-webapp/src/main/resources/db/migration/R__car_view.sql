create view rent_car.car_view
as
select c_pub.number                  as number,
       c_pub.renter                  as renter,
       c_pub.name                    as name,
       c_priv.renter_passport_number as renter_passport_number,
       c_priv.tx_id                  as tx_id,
       c_priv.policy_id              as policy_id
from rent_car.car_public c_pub
         join rent_car.car_private c_priv on c_pub.number = c_priv.number;
