package twibs

import org.scalatest.matchers.{MatchResult, Matcher, BePropertyMatchResult, BePropertyMatcher}
import org.scalatest.{FunSuite, Matchers}
import util.Logger

trait TwibsTest extends FunSuite with Matchers {
  Logger.init()

  def anInstanceOf[T](implicit manifest: Manifest[T]) = {
    val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName)
    }
  }

  def beTrue: Matcher[Boolean] =
    new Matcher[Boolean] {
      def apply(left: Boolean) =
        MatchResult(
          left,
          "was not true",
          "was true"
        )
    }

  def beFalse: Matcher[Boolean] =
    new Matcher[Boolean] {
      def apply(left: Boolean) =
        MatchResult(
          !left,
          "was not false",
          "was false"
        )
    }
}
