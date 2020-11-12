case class LC(roommates: Person*) {
  def generateBill(outlays: Outlay*)={
    val initalSituation = getInitalSituation(roommates)
    val finalSituation = getFinalSituation(initalSituation, outlays)
    finalSituation
  }
  def getInitalSituation(persons: Seq[Person]): Vector[Float] ={
    Vector.fill(persons.length)(0)
  }
  def getFinalSituation(initalSituation: Vector[Float], outlays: Seq[Outlay]) ={
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
