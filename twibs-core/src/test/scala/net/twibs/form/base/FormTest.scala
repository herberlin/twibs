/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.form.base

import net.twibs.testutil.TwibsTest

class FormTest extends TwibsTest {
  test("Component state") {
    def check(cs: ComponentState, enabled: Boolean, disabled: Boolean, hidden: Boolean, ignored: Boolean) = {
      cs.isEnabled should be(enabled)
      cs.isDisabled should be(disabled)
      cs.isHidden should be(hidden)
      cs.isIgnored should be(ignored)
    }

    check(ComponentState.Enabled, enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Disabled, enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Hidden, enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Ignored, enabled = false, disabled = true, hidden = true, ignored = true)

    check(ComponentState.Enabled ~ ComponentState.Ignored, enabled = false, disabled = true, hidden = true, ignored = true)
    check(ComponentState.Enabled ~ ComponentState.Hidden, enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Enabled ~ ComponentState.Disabled, enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Enabled.ignoreIf(condition = true), enabled = false, disabled = true, hidden = true, ignored = true)
    check(ComponentState.Enabled.hideIf(condition = true), enabled = false, disabled = true, hidden = true, ignored = false)
    check(ComponentState.Enabled.disableIf(condition = true), enabled = false, disabled = true, hidden = false, ignored = false)
    check(ComponentState.Enabled.ignoreIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Enabled.hideIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Enabled.disableIf(condition = false), enabled = true, disabled = false, hidden = false, ignored = false)
    check(ComponentState.Disabled.disableIf(condition = throw new RuntimeException()), enabled = false, disabled = true, hidden = false, ignored = false)
    intercept[RuntimeException] {
      check(ComponentState.Enabled.disableIf(condition = throw new RuntimeException()), enabled = false, disabled = true, hidden = false, ignored = false)
    }
  }
}
