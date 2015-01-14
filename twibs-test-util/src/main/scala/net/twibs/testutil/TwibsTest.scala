/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.testutil

import net.twibs.util.Logger
import org.scalatest.matchers.{BePropertyMatchResult, BePropertyMatcher, MatchResult, Matcher}
import org.scalatest.{FunSuite, Matchers}

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
