import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import implicitFunctions._

class OutlayTest extends AnyWordSpec with Matchers{
  val outlay = Outlay(None, None, None, None)
  "The function 'from' should return a Outlay object with Paul, if 'Paul' is passed" in{
    val result_outlay = Outlay(Some("Paul"), None, None, None)
    outlay from "Paul" should be(result_outlay)
  }
  "The function 'towards' should return a Outlay object with 'Martin, Till', if 'Martin, Till' is passed" in{
    val result_outlay = Outlay(None, Some(Seq("Martin","Till")), None, None)
    outlay towards ("Martin","Till") should be(result_outlay)
  }
  "The function 'of' should return a Outlay object with '100', if '100.EUR' is passed" in{
    val result_outlay = Outlay(None, None, Some(100), None)
    outlay of 100.EUR should be(result_outlay)
  }
  "The function 'of' should return a Outlay object with '92', if '100.CHF' is passed" in{
    val result_outlay = Outlay(None, None, Some(93), None)
    outlay of 100.CHF should be(result_outlay)
  }
  "The function 'at' should return a Outlay object with 'supermarket', if 'supermarket' is passed" in{
    val result_outlay = Outlay(None, None, None, Some("Supermarket"))
    outlay at "Supermarket" should be(result_outlay)
  }
  "The function chain should return a Outlay object" in{
    val result_outlay = Outlay(Some("Martin"), Some(Seq("Till", "Paul", "Martin")), Some(222), Some("cinema"))
    outlay from "Martin" towards ("Till", "Paul", "Martin") of 222.EUR at "cinema" should be(result_outlay)
  }

}
