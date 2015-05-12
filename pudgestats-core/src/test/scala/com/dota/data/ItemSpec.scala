package com.dota.data 

import org.scalatest.FunSuite

class ItemSpec
  extends FunSuite
{
  test("Recipe (Bracer) should have an Item associated with it.") {
    assert(!Item.fromString("item_recipe_bracer").isEmpty)
  }
}
