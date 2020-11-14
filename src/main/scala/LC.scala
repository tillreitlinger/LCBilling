import javax.tools.DocumentationTool.Location

case class LC(roommates: String*) {

  def generateBill(outlays: Outlay*)={
    val initalSituation = getInitalSituation(roommates)
    val bill = getFinalSituation(initalSituation, outlays)
    val sum = generateSum(bill)
    val locations = getLocations(outlays)
    generateBillDocument(bill, sum, locations)
  }

  def generateSum(bill:Seq[Vector[Float]])={
    val result = bill.toList.transpose.map(_.sum)
    result
  }


  def generateBillDocument(bill:Seq[Vector[Float]], sum: List[Float], location: Seq[String])={
    val names = "\t" + roommates.map(person => person).mkString("\t") +"\n"
    val billString = bill.map(outlay => outlay.map(amount => "\t" + f"$amount%1.2f")).zipWithIndex.map{
      case (n, index) => n.mkString("") + "\t" + location(index)
    }.mkString("\n")
    val sumString = "\n\t" + sum.map(sumOfPerson => f"$sumOfPerson%1.2f").mkString("\t") + "\n"
    val resultString = getPayInstuctions(sum)
    names + billString + sumString +resultString
  }

  def getPayInstuctions( sum:List[Float])={
    val payInstructionString = roommates.zipWithIndex.map{
      case (mate, index) =>
        if (sum(index) < 0){
          mate + " has to pay " + sum(index) + " Euro\n"
        }
        else{
          mate + " has to receive " + sum(index) + " Euro\n"
        }
    }
    payInstructionString.mkString("")
  }
  def getInitalSituation(persons: Seq[String]): Vector[Float] ={
    Vector.fill(persons.length)(0)
  }

  def getLocations(outlays: Seq[Outlay])={
    val locations = outlays.map{
      overlay => overlay.at.get
    }
    locations
  }
  def getFinalSituation(initalSituation: Vector[Float], outlays: Seq[Outlay]):Seq[Vector[Float]] ={
    val complete_list = outlays.map{
      overlay => {
        val payedFrom = roommates.indexOf(overlay.payedFrom.get)
        val amount = overlay.amount.get
        val numberOfIvolved = overlay.payedFor.get.length
        val involvedPeople = overlay.payedFor.get.map(person => roommates.indexOf(person))
        val subAmount = initalSituation.zipWithIndex.map{
          case (initalAmount, index) =>
            if (involvedPeople.contains(index)) -amount/numberOfIvolved
            else initalAmount
        }
        val final_row = subAmount.zipWithIndex.map{
          case (initialAmount, index) =>
            if (payedFrom == index) initialAmount+amount
            else initialAmount}
        final_row
      }
    }
    complete_list
  }
}
