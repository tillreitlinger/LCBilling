//case class FinanceStatus(financeSituation: Vector[Vector[Float]], roommates: Seq[Person]) {
case class FinanceStatus(status: Vector[Vector[Float]]){
  def updateStaus(person: Person, amount: Float):FinanceStatus = {
    copy(Vector(Vector(status(0)(0), amount, status(0)(2))))
  }
}
