/**
 * This software is Copyright (C) 2016 Tod G. Harter. All rights reserved.
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

import java.nio.file.Path;

import com.giantelectronicbrain.catfood.buckets.IBucketName;
import com.giantelectronicbrain.catfood.buckets.IBucketObjectName;

import lombok.Builder;
import lombok.Getter;

/**
 * @author tharter
 *
 */
@Builder
@Getter
public class FsBucketObjectName implements IBucketObjectName {

	 private final String name;
	 private final IBucketName bucketName;
	 
	 protected FsBucketObjectName(String name, IBucketName bucketName) {
		 this.name = name;
		 this.bucketName = bucketName;
	 }
	 
	 public String getName() {
		 return this.name;
	 }
	 
}
