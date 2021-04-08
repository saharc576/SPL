import sqlite3
from DTO import Clinic, Vaccine, Logistic, Supplier

# ====================== Clinics ======================


class _Clinics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, clinicDTO):
        self._conn.execute(""" 
        INSERT INTO clinics (id, location, demand, logistic) 
        VALUES (?, ?, ?, ?)""", [clinicDTO.id, clinicDTO.location, clinicDTO.demand, clinicDTO.logistic])

    def find(self, location):
        c = self._conn.cursor()
        c.execute(""" 
        SELECT * FROM clinics WHERE location = ?
        """, [location])
        data = c.fetchone()
        return Clinic(data[0], data[1], data[2], data[3])

    # a shipment was received, reduce demand
    def receive_vaccines(self, location, amount_rec):
        c = self._conn.cursor()
        curr_data = self.find(location)
        curr_amount = curr_data.demand
        c.execute("""UPDATE clinics
                            SET demand = ?
                            WHERE location = ? """, [curr_amount - amount_rec, location])

    def get_sum_demand(self):
        c = self._conn.cursor()
        c.execute("""SELECT
                    SUM(demand) 
                    FROM clinics """)
        return str(c.fetchone()[0])

# ====================== Logistics ======================


class _Logistics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, logisticDTO):
        c = self._conn.cursor()
        c.execute(""" 
        INSERT INTO logistics (id, name, count_sent, count_received) 
        VALUES (?, ?, ?, ?)""", [logisticDTO.id, logisticDTO.name, logisticDTO.count_sent, logisticDTO.count_received])

    def find(self, id):
        c = self._conn.cursor()
        c.execute(""" 
        SELECT id, name, count_sent, count_received FROM logistics WHERE id = ?""", [id])
        data = c.fetchone()
        return Logistic(data[0], data[1], data[2], data[3])

    def inc_count_sent(self, id, count_sent_to_add):
        c = self._conn.cursor()
        curr_count_sent = int(self.find(id).count_sent)
        c.execute("""UPDATE logistics
                            SET count_sent = ?
                            WHERE id = ? """, [curr_count_sent + int(count_sent_to_add), id])

    def inc_count_rec(self, id, count_rec_to_add):
        c = self._conn.cursor()
        curr_data = self.find(id)
        curr_count_rec = curr_data.count_received
        c.execute("""UPDATE logistics
                            SET count_received = ?
                            WHERE id = ? """, [curr_count_rec + count_rec_to_add, id])

    def get_total_rec(self):
        c = self._conn.cursor()
        c.execute("""SELECT
                    SUM(count_received) 
                    FROM logistics """)
        return str(c.fetchone()[0])

    def get_total_sent(self):
        c = self._conn.cursor()
        c.execute("""SELECT
                    SUM(count_sent) 
                    FROM logistics """)
        return str(c.fetchone()[0])

# ====================== Suppliers ======================


class _Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplierDTO):
        self._conn.execute(""" 
        INSERT INTO suppliers (id, name, logistic) 
        VALUES (?, ?, ?)""", [supplierDTO.id, supplierDTO.name, supplierDTO.logistic])

    def find(self, name):
        c = self._conn.cursor()
        c.execute(""" 
        SELECT * FROM suppliers WHERE name = ?""", [name])
        data = c.fetchone()
        return Supplier(data[0], data[1], data[2])


# ====================== Vaccines ======================


class _Vaccines:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, vaccineDTO):
        self._conn.execute(""" 
        INSERT INTO vaccines (id, date, supplier, quantity) 
        VALUES (?, ?, ?, ?)""", [int(vaccineDTO.id), vaccineDTO.date, vaccineDTO.supplier, int(vaccineDTO.quantity)])

    def find(self, id):
        c = self._conn.cursor()
        c.execute(""" 
        SELECT * FROM vaccines WHERE id = ?""", [id])
        data = c.fetchone()
        return Vaccine(data[0], data[1], data[2], data[3])

    def get_sum_meds(self):
        c = self._conn.cursor()
        c.execute("""SELECT
                    SUM(quantity) 
                    FROM vaccines """)
        return str(c.fetchone()[0])

    def inc_quantity(self, id_, quantity_to_add):
        c = self._conn.cursor()
        curr_data = self.find(id_)
        curr_quantity = curr_data.quantity
        c.execute("""UPDATE vaccines
                    SET quantity = ?
                    WHERE id = ? """, [curr_quantity + quantity_to_add, id_])

    def dec_quantity(self, id_, quantity_to_dec):
        c = self._conn.cursor()
        curr_data = self.find(id_)
        curr_quantity = int(curr_data.quantity)
        c.execute("""UPDATE vaccines
                       SET quantity = ?
                       WHERE id = ? """, [curr_quantity - quantity_to_dec, id_])

    def get_vaccines(self, amount):
        c = self._conn.cursor()
        c.execute("""SELECT
                        *
                        FROM vaccines
                        ORDER BY date ASC """)
        res = c.fetchall()
        for v in res:
            vaccine = Vaccine(*v)
            if vaccine.quantity > amount:
                self.dec_quantity(vaccine.id, amount)
                break
            else:
                amount = amount - vaccine.quantity
                c.execute("""DELETE FROM vaccines WHERE id = ?""", [vaccine.id])
