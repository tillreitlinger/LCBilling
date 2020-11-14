import javax.tools.DocumentationTool.Location

case class LC(roommates: String*) {

  def generateBill(outlays: Outlay*)={
    val initalSituation = getInitalSituation(roommates)
    val bill = getFinalSituation(initalSituation, outlays)
    val sum = generateSum(bill)
    val locations = getLocations(outlays)
    generateBillDocument(bill, sum, locations)
  }

  def generateBillDocument(bill:Seq[Vector[Float]], sum: List[Float], location: Seq[String])={
    val names = createNameString()
    val billString = createBillString(bill, location)
    val sumString = createSumString(sum)
    val resultString = getPayInstuctions(sum)
    names + billString + sumString +resultString
  }

  def getPayInstuctions( sum:List[Float])={
    val payInstructionString = roommates.zipWithIndex.map{
      case (mate, index) =>
        if(personIsInDept(sum, index)){
          mate + " has to pay " + sum(index) + " Euro\n"
        }
        else{
          mate + " has to receive " + sum(index) + " Euro\n"
        }
    }
    payInstructionString.mkString("")
  }

  def getFinalSituation(initalSituation: Vector[Float], outlays: Seq[Outlay]):Seq[Vector[Float]] ={
    val complete_list = outlays.map{
      overlay => {
        val payedFrom = roommates.indexOf(overlay.payedFrom.get)
        val amount = overlay.amount.get
        val numberOfIvolved = overlay.payedFor.get.length
        val involvedPeople = overlay.payedFor.get.map(person => roommates.indexOf(person))
        val subAmount = getSubstractionAmountForEachPerson(initalSituation, involvedPeople, numberOfIvolved, amount)
        val final_row = addAmountToPersonWhoPayed(subAmount, payedFrom, amount)
        final_row
      }
    }
    complete_list
  }

  def addAmountToPersonWhoPayed(subAmount:Vector[Float], payedFrom:Int, amount:Float):Vector[Float]={
    subAmount.zipWithIndex.map{
      case (initialAmount, index) =>
        if (payedFrom == index) initialAmount+amount
        else initialAmount}
  }

  def getSubstractionAmountForEachPerson(initalSituation: Vector[Float], involvedPeople: Seq[Int], numberOfIvolved:Int, amount:Float):Vector[Float]={
    initalSituation.zipWithIndex.map{
      case (initalAmount, index) =>
        if (involvedPeople.contains(index)) -amount/numberOfIvolved
        else initalAmount
    }
  }

  def getLocations(outlays: Seq[Outlay])={
    val locations = outlays.map{
      overlay => overlay.at.get
    }
    locations
  }

  def generateSum(bill:Seq[Vector[Float]])={
    val result = bill.toList.transpose.map(_.sum)
    result
  }

  def createSumString(sum: List[Float])={
    "\n\t" + sum.map(sumOfPerson => f"$sumOfPerson%1.2f").mkString("\t") + "\n"
  }

  def createNameString()={
    "\t" + roommates.map(person => person).mkString("\t") +"\n"
  }

  def createBillString(bill:Seq[Vector[Float]], location: Seq[String])={
    bill.map(outlay => outlay.map(amount => "\t" + f"$amount%1.2f")).zipWithIndex.map{
      case (n, index) => n.mkString("") + "\t" + location(index)
    }.mkString("\n")
  }

  def personIsInDept(sum:List[Float], index:Int)={
    (sum(index) < 0)
  }

  def getInitalSituation(persons: Seq[String]): Vector[Float] ={
    Vector.fill(persons.length)(0)
  }
}
