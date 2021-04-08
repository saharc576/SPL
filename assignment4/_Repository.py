import sqlite3
import atexit
from DAO import _Clinics, _Logistics, _Suppliers, _Vaccines


class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect('database.db')
        self.vaccines = _Vaccines(self._conn)
        self.suppliers = _Suppliers(self._conn)
        self.clinics = _Clinics(self._conn)
        self.logistics = _Logistics(self._conn)

    def close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE vaccines (
                                id INTEGER PRIMARY KEY,
                                date DATE NOT NULL,
                                supplier INTEGER,
                                quantity INTEGER NOT NULL,
                                FOREIGN KEY(supplier) REFERENCES suppliers(id)
                                );
                                
        CREATE TABLE suppliers (
                                id INTEGER PRIMARY KEY,  
                                name TEXT NOT NULL,
                                logistic INTEGER,
                                FOREIGN KEY(logistic) REFERENCES logistics(id)
                                );
                                
        CREATE TABLE clinics (
                                id INTEGER PRIMARY KEY,  
                                location TEXT NOT NULL,
                                demand INTEGER NOT NULL, 
                                logistic INTEGER,
                                FOREIGN KEY(logistic) REFERENCES logistics(id)
                                );
        
        CREATE TABLE logistics (
                                id INTEGER PRIMARY KEY,  
                                name TEXT NOT NULL,
                                count_sent INTEGER NOT NULL,
                                count_received INTEGER NOT NULL
                                );""")


repo = _Repository()
atexit.register(repo.close)
