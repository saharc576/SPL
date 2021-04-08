class Logistic:
    def __init__(self, id_, name, count_sent, count_received):
        self.id = id_
        self.name = name
        self.count_sent = count_sent
        self.count_received = count_received


class Supplier:
    def __init__(self, id_, name, logistic):
        self.id = id_
        self.name = name
        self.logistic = logistic


class Vaccine:
    def __init__(self, id_, date, supplier, quantity):
        self.id = id_
        self.date = date
        self.supplier = supplier
        self.quantity = quantity


class Clinic:
    def __init__(self, id_, location, demand, logistic):
        self.id = id_
        self.location = location
        self.demand = demand
        self.logistic = logistic
