import sys
from DTO import Clinic, Vaccine, Logistic, Supplier
from _Repository import repo


def summarize_action(counter):
    total_inventory = repo.vaccines.get_sum_meds()
    total_demand = repo.clinics.get_sum_demand()
    total_received = repo.logistics.get_total_rec()
    total_sent = repo.logistics.get_total_sent()
    if counter > 1:
        return str(total_inventory) + "," + str(total_demand) + "," + str(total_received) + "," + str(total_sent) + "\n"
    else:
        return str(total_inventory) + "," + str(total_demand) + "," + str(total_received) + "," + str(total_sent)


# add a new shipment to vaccines and inc the amount sent by this logistic


def receive_ship(id_, name, amount, date):
    supplier_id = repo.suppliers.find(name).id
    repo.vaccines.insert(Vaccine(id_, date, supplier_id, amount))  # insert a new shipment
    logistic = repo.suppliers.find(name).logistic  # find the logistic that received
    repo.logistics.inc_count_rec(logistic, amount)  # increment it's received amount


def send_ship(location, amount):
    logistic = repo.clinics.find(location).logistic  # find the logistic
    repo.logistics.inc_count_sent(logistic, amount)  # increment the amount sent by this logistic
    repo.vaccines.get_vaccines(amount)  # get the oldest vaccines
    repo.clinics.receive_vaccines(location, amount)  # reduce the demand in the location that received the ship


if __name__ == "__main__":
    with open(sys.argv[1], "r") as configFile:
        file = configFile.read().split("\n")
        count = countV = countS = countC = countL = numID = 0
        repo.create_tables()

        for line in file:
            dataToInsert = line.split(",")
            if count == 0:
                countV = int(dataToInsert[0])
                countS = int(dataToInsert[1])
                countC = int(dataToInsert[2])
                countL = int(dataToInsert[3])
                count += 1
            elif countV > 0:
                v = Vaccine(dataToInsert[0], dataToInsert[1], dataToInsert[2], dataToInsert[3])
                repo.vaccines.insert(v)
                numID = int(dataToInsert[0])
                countV -= 1
            elif countS > 0:
                s = Supplier(dataToInsert[0], dataToInsert[1], dataToInsert[2])
                repo.suppliers.insert(s)
                countS -= 1
            elif countC > 0:
                c = Clinic(dataToInsert[0], dataToInsert[1], dataToInsert[2], dataToInsert[3])
                repo.clinics.insert(c)
                countC -= 1
            elif countL > 0:
                log = Logistic(dataToInsert[0], dataToInsert[1], dataToInsert[2], dataToInsert[3])
                repo.logistics.insert(log)
                countL -= 1

    outputFile = open(sys.argv[3], "w")
    with open(sys.argv[2], "r") as inputFile:
        file = inputFile.read().split("\n")
        n_orders = len(file)
        for order in file:
            dataToInsert = order.split(",")
            if len(dataToInsert) == 3:
                numID = numID + 1
                receive_ship(numID, dataToInsert[0], int(dataToInsert[1]), dataToInsert[2])
            else:
                send_ship(dataToInsert[0], int(dataToInsert[1]))
            output = summarize_action(n_orders)
            outputFile.write(output)
            n_orders -= 1

    outputFile.close()
