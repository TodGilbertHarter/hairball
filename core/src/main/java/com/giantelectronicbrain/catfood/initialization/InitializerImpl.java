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

package com.giantelectronicbrain.catfood.initialization;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tharter
 *
 */
public class InitializerImpl implements IInitializer {
	Map<Object,Object> configMap = new HashMap<Object,Object>();
		
	/* (non-Javadoc)
	 * @see com.boeing.aims.vertxdemo.config.IConfigurator#get(java.lang.Object)
	 */
	@Override
	public Object get(Object configKey) throws InitializationException {
		if(!configMap.containsKey(configKey))
			throw new InitializationException("Cannot initialize, no object with key "+configKey);
		return configMap.get(configKey);
	}

	/* (non-Javadoc)
	 * @see com.boeing.aims.vertxdemo.config.IConfigurator#set(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void set(Object configKey, Object configValue) {
		configMap.put(configKey, configValue);
	}

	@Override
	public void print() {
		configMap.keySet().stream().sorted().forEachOrdered(key -> {
			System.out.println(key+"="+configMap.get(key));
		});
	}

}
