/* 
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */

package com.example.flickrlibrary.model

import com.google.gson.annotations.SerializedName

data class GalleryBase(

	@SerializedName("galleries") val galleries: Galleries,
	@SerializedName("stat") val stat: String
)

data class Gallery(

	@SerializedName("id") val id: String,
	@SerializedName("gallery_id") val gallery_id: String,
	@SerializedName("url") val url: String,
	@SerializedName("owner") val owner: String,
	@SerializedName("username") val username: String,
	@SerializedName("iconserver") val iconserver: Int,
	@SerializedName("iconfarm") val iconfarm: Int,
	@SerializedName("primary_photo_id") val primary_photo_id: String,
	@SerializedName("date_create") val date_create: Int,
	@SerializedName("date_update") val date_update: Int,
	@SerializedName("count_photos") val count_photos: Int,
	@SerializedName("count_videos") val count_videos: Int,
	@SerializedName("count_total") val count_total: Int,
	@SerializedName("count_views") val count_views: Int,
	@SerializedName("count_comments") val count_comments: Int,
	@SerializedName("title") val title: Title,
	@SerializedName("description") val description: Description,
	@SerializedName("sort_group") val sort_group: String,
	@SerializedName("primary_photo_server") val primary_photo_server: Int,
	@SerializedName("primary_photo_farm") val primary_photo_farm: Int,
	@SerializedName("primary_photo_secret") val primary_photo_secret: String
)

data class Galleries(

	@SerializedName("total") val total: Int,
	@SerializedName("per_page") val per_page: Int,
	@SerializedName("user_id") val user_id: String,
	@SerializedName("page") val page: Int,
	@SerializedName("pages") val pages: Int,
	@SerializedName("gallery") val gallery: List<Gallery>
)

data class Description(

	@SerializedName("_content") val _content: String
)

data class Title(

	@SerializedName("_content") val _content: String
)