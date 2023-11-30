/**
 * This software is Copyright (C) 2017 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.giantelectronicbrain.catfood.buckets.fs;

import java.io.IOException;

import com.giantelectronicbrain.catfood.buckets.BucketDriverException;
import com.giantelectronicbrain.catfood.buckets.BucketTest;
import com.giantelectronicbrain.catfood.buckets.IBucketDriver;

/**
 * @author tharter
 *
 */
public class FsBucketTest extends BucketTest {

	/* (non-Javadoc)
	 * @see com.boeing.bms.goldstandard.buckets.BucketTest#createUUT()
	 */
	@Override
	protected IBucketDriver createUUT() throws BucketDriverException {
		return FsBucketTestUtils.createUUT();
	}

	@Override
	protected void cleanUpBuckets() throws IOException {
		FsBucketTestUtils.cleanUpBuckets();
	}
	
	@Override
	protected void setUpBuckets() throws IOException {
		FsBucketTestUtils.setUpBuckets();
	}
}
