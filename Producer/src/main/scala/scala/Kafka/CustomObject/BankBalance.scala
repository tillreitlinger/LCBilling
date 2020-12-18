package scala.Kafka.CustomObject

class BankBalance {
  private var bankBalance:String = ""

    def this(bankBalance: String) {
      this()
      this.bankBalance = bankBalance
    }

    def getBankBalance: String = this.bankBalance
}
