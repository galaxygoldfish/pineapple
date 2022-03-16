package com.pineapple.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pineapple.app.R
import com.pineapple.app.model.PostData
import com.pineapple.app.util.prettyNumber

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
fun TextPostCard(postData: PostData) {
    Card(
        shape = RoundedCornerShape(10.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = postData.author,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "r/${postData.subreddit}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = postData.title,
                maxLines = 3,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                    .data(postData.urlOverriddenByDest)
                    .placeholder(R.drawable.placeholder_image)
                    .crossfade(true)
                    .build().data,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                PostCardIconButton(
                    icon = painterResource(id = R.drawable.ic_comments_bubble),
                    contentDescription = stringResource(id = R.string.ic_comments_bubble_content_desc),
                    text = postData.numComments.toInt().prettyNumber()
                )
                PostCardIconButton(
                    icon = painterResource(id = R.drawable.ic_thumbs_up),
                    contentDescription = stringResource(id = R.string.ic_thumbs_up_content_desc),
                    text = postData.ups.toInt().prettyNumber()
                )
            }
            Row(modifier = Modifier.padding(end = 15.dp)) {
                PostCardIconButton(
                    icon = painterResource(id = R.drawable.ic_share_glyph),
                    contentDescription = stringResource(id = R.string.ic_share_glyph_content_desc),
                    text = ""
                )
            }
        }
    }
}

@Composable
fun PostCardIconButton(
    icon: Painter,
    contentDescription: String,
    text: String,
    onClick: (() -> Unit) = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 15.dp, top = 10.dp, bottom = 10.dp)
            .clickable { onClick.invoke() }
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}