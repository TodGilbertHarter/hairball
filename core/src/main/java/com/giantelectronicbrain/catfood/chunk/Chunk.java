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

package com.giantelectronicbrain.catfood.chunk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * DTO representing a chunk of CatFood content.
 * 
 * @author tharter
 *
 */
//@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Chunk {
	public enum Language {
		MARKDOWN,
		HTML,
		HAIRBALL
	}

	@JsonUnwrapped
	private ChunkId chunkId;
	private Long lastUpdated;
	private String content;
	private String name;
	private Language lang;
	
	/**
	 * Instantiate a content chunk.
	 */
	public Chunk() {
	}

	/**
	 * Instantiate a chunk with the given content.
	 * 
	 * @param content initial content for this chunk
	 * @param the ChunkId of this chunk, or null if it hasn't got one.
	 * @param name the name of the chunk
	 * @param language the language of the chunk
	 * @param long last updated time in unix epoch milliseconds
	 */
	public Chunk(String content, ChunkId chunkId, String name, Language language, long lastUpdated) {
		setContent(content);
		setChunkId(chunkId);
		setName(name);
		setLang(language);
		setLastUpdated(lastUpdated);
	}

	/**
	 * Get the id of the chunk as an html legal id attribute value. If no id exists
	 * for this chunk, then return "null_chunk_id". 
	 * 
	 * @return legal chunk id string value
	 */
	public String getChunkIdAsHtml() {
		return getChunkId() == null ? "null_chunk_id" : getChunkId().getChunkIdAsHtml(); 
	}
	
	/**
	 * @return the topic
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the language
	 */
	public Language getLang() {
		return lang;
	}

	/**
	 * @param language the language to set
	 */
	public void setLang(Language lang) {
		this.lang = lang;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@JsonProperty("@rid")
	public ChunkId getChunkId() {
		return chunkId;
	}

	@JsonProperty("@rid")
	public void setChunkId(ChunkId chunkId) {
		this.chunkId = chunkId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chunkId == null) ? 0 : chunkId.hashCode());
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (chunkId == null) {
			if (other.chunkId != null)
				return false;
		} else if (!chunkId.equals(other.chunkId))
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (lang != other.lang)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Chunk [chunkId=" + chunkId + ", content=" + content + ", name=" + name + ", lang=" + lang + "]";
	}

	/**
	 * @return the lastUpdated
	 */
	public Long getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	
}
