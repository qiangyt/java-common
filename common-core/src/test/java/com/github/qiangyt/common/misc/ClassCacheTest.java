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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.github.qiangyt.common.err.BadValueException;

public class ClassCacheTest {

	class Sample {
	}

	@Test
	public void test_happy() {
		var t = new ClassCache();

		assertSame(Sample.class, t.resolve(Sample.class.getName()));
		assertEquals(1, t.size());

		assertSame(Sample.class, t.resolve(Sample.class.getName()));
		assertEquals(1, t.size());

		assertThrows(BadValueException.class, () -> t.resolve(Sample.class.getName() + "NotIndeedExists"));
		assertEquals(1, t.size());

	}

}
