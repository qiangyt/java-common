/*
 * io.github.qiangyt:qiangyt-common-core - Common library by Yiting Qiang
 * Copyright © 2023 Yiting Qiang (qiangyt@wxcount.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.qiangyt.common.misc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DurationHelperTest {

	@Test
	public void test_parse() {
		assertNull(DurationHelper.parse(null));
		assertNull(DurationHelper.parse(" "));

		assertEquals(1, DurationHelper.parse("1s").getSeconds());
		assertEquals(2 * 60, DurationHelper.parse("2m").getSeconds());
		assertEquals(3 * 3600, DurationHelper.parse("3h").getSeconds());
		assertEquals(4 * 86400, DurationHelper.parse("4d").getSeconds());
		assertEquals(5 * 7 * 86400, DurationHelper.parse("5w").getSeconds());

		assertEquals(1 * 7 * 86400 + 2 * 86400 + 3 * 3600 + 4 * 60 + 5,
			DurationHelper.parse("1w2d3h4m5s").getSeconds());
	}

}
