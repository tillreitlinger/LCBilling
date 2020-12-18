package scala

case class Outlay(payedFrom: Option[String], payedFor: Option[Seq[String]], amount: Option[Float], at: Option[String]) {
  def from(person: String) = {
    copy(Some(person), payedFor, amount, at)
  }

  def towards(persons: List[String]) = {
    copy(payedFrom, Some(persons), amount, at)
  }

  def of(amount: Float) = {
    copy(payedFrom, payedFor, Some(amount), at)
  }

  def at(place: String) = {
    copy(payedFrom, payedFor, amount, Some(place))
  }
}
