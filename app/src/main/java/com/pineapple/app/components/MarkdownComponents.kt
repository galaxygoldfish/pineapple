package com.pineapple.app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import org.commonmark.node.*
import org.commonmark.node.Paragraph

/**
 * Sourced from
 * https://github.com/ErikHellman/MarkdownComposer/
 * Original author: Erik Hellman
 * With modifications made
 */

private const val TAG_URL = "url"
private const val TAG_IMAGE_URL = "imageUrl"

@Composable
fun MDDocument(document: Document) {
    MDBlockChildren(document)
}

@Composable
fun MDHeading(heading: Heading, modifier: Modifier = Modifier) {
    val style = when (heading.level) {
        1 -> MaterialTheme.typography.headlineLarge
        2 -> MaterialTheme.typography.headlineMedium
        3 -> MaterialTheme.typography.headlineSmall
        4 -> MaterialTheme.typography.titleLarge
        5 -> MaterialTheme.typography.titleMedium
        6 -> MaterialTheme.typography.titleSmall
        else -> {
            // Invalid header...
            MDBlockChildren(heading)
            return
        }
    }
    Box(modifier = modifier.padding(bottom = 8.dp)) {
        val text = buildAnnotatedString {
            appendMarkdownChildren(heading, MaterialTheme.colorScheme)
        }
        MarkdownText(text, style)
    }
}

@Composable
fun MDParagraph(paragraph: Paragraph, modifier: Modifier = Modifier) {
    if (paragraph.firstChild is Image && paragraph.firstChild == paragraph.lastChild) {
        // Paragraph with single image
        MDImage(paragraph.firstChild as Image, modifier)
    } else {
        Box(modifier = modifier.padding(bottom = 5.dp)) {
            val styledText = buildAnnotatedString {
                pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
                appendMarkdownChildren(paragraph, MaterialTheme.colorScheme)
                pop()
            }
            MarkdownText(styledText, MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun MDImage(image: Image, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Image(
            painter = rememberImagePainter(
                data = image.destination,
                builder = {
                    size(OriginalSize)
                },
            ),
            contentDescription = null,
        )
    }
}

@Composable
fun MDBulletList(bulletList: BulletList, modifier: Modifier = Modifier) {
    val marker = bulletList.bulletMarker
    MDListItems(bulletList, modifier = modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
            append("$marker ")
            appendMarkdownChildren(it, MaterialTheme.colorScheme)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.bodyLarge, modifier)
    }
}

@Composable
fun MDOrderedList(orderedList: OrderedList, modifier: Modifier = Modifier) {
    var number = orderedList.startNumber
    val delimiter = orderedList.delimiter
    MDListItems(orderedList, modifier) {
        val text = buildAnnotatedString {
            pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle())
            append("${number++}$delimiter ")
            appendMarkdownChildren(it, MaterialTheme.colorScheme)
            pop()
        }
        MarkdownText(text, MaterialTheme.typography.bodyLarge, modifier)
    }
}

@Composable
fun MDListItems(
    listBlock: ListBlock,
    modifier: Modifier = Modifier,
    item: @Composable (node: Node) -> Unit
) {
    val bottom = if (listBlock.parent is Document) 8.dp else 0.dp
    val start = if (listBlock.parent is Document) 0.dp else 8.dp
    Column(modifier = modifier.padding(start = start, bottom = bottom)) {
        var listItem = listBlock.firstChild
        while (listItem != null) {
            var child = listItem.firstChild
            while (child != null) {
                when (child) {
                    is BulletList -> MDBulletList(child, modifier)
                    is OrderedList -> MDOrderedList(child, modifier)
                    else -> item(child)
                }
                child = child.next
            }
            listItem = listItem.next
        }
    }
}

@Composable
fun MDBlockQuote(blockQuote: BlockQuote, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onBackground
    Box(modifier = modifier
        .drawBehind {
            drawLine(
                color = color,
                strokeWidth = 2f,
                start = Offset(12.dp.value, 0f),
                end = Offset(12.dp.value, size.height)
            )
        }
        .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
        val text = buildAnnotatedString {
            pushStyle(
                MaterialTheme.typography.bodyLarge.toSpanStyle()
                    .plus(SpanStyle(fontStyle = FontStyle.Italic))
            )
            appendMarkdownChildren(blockQuote, MaterialTheme.colorScheme)
            pop()
        }
        Text(text, modifier)
    }
}

@Composable
fun MDFencedCodeBlock(fencedCodeBlock: FencedCodeBlock, modifier: Modifier = Modifier) {
    val padding = if (fencedCodeBlock.parent is Document) 8.dp else 0.dp
    Box(modifier = modifier.padding(start = 8.dp, bottom = padding)) {
        Text(
            text = fencedCodeBlock.literal,
            style = TextStyle(fontFamily = FontFamily.Monospace),
            modifier = modifier
        )
    }
}

@Composable
fun MDIndentedCodeBlock(indentedCodeBlock: IndentedCodeBlock, modifier: Modifier = Modifier) {
    // Ignored
}

@Composable
fun MDThematicBreak(thematicBreak: ThematicBreak, modifier: Modifier = Modifier) {
    //Ignored
}

@Composable
fun MDBlockChildren(parent: Node) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is BlockQuote -> MDBlockQuote(child)
            is ThematicBreak -> MDThematicBreak(child)
            is Heading -> MDHeading(child)
            is Paragraph -> MDParagraph(child)
            is FencedCodeBlock -> MDFencedCodeBlock(child)
            is IndentedCodeBlock -> MDIndentedCodeBlock(child)
            is Image -> MDImage(child)
            is BulletList -> MDBulletList(child)
            is OrderedList -> MDOrderedList(child)
        }
        child = child.next
    }
}

fun AnnotatedString.Builder.appendMarkdownChildren(parent: Node, colors: ColorScheme) {
    var child = parent.firstChild
    while (child != null) {
        when (child) {
            is Paragraph -> appendMarkdownChildren(child, colors)
            is Text -> append(child.literal)
            is Image -> appendInlineContent(TAG_IMAGE_URL, child.destination)
            is Emphasis -> {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is StrongEmphasis -> {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                appendMarkdownChildren(child, colors)
                pop()
            }
            is Code -> {
                pushStyle(TextStyle(fontFamily = FontFamily.Monospace).toSpanStyle())
                append(child.literal)
                pop()
            }
            is HardLineBreak -> {
                append("\n")
            }
            is Link -> {
                val underline = SpanStyle(colors.primary, textDecoration = TextDecoration.Underline)
                pushStyle(underline)
                pushStringAnnotation(TAG_URL, child.destination)
                appendMarkdownChildren(child, colors)
                pop()
                pop()
            }
        }
        child = child.next
    }
}

@Composable
fun MarkdownText(text: AnnotatedString, style: TextStyle, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text = text,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures { offset ->
                layoutResult.value?.let { layoutResult ->
                    val position = layoutResult.getOffsetForPosition(offset)
                    text.getStringAnnotations(position, position)
                        .firstOrNull()
                        ?.let { sa ->
                            if (sa.tag == TAG_URL) {
                                uriHandler.openUri(sa.item)
                            }
                        }
                }
            }
        }.padding(bottom = 5.dp),
        style = style,
        inlineContent = mapOf(
            TAG_IMAGE_URL to InlineTextContent(
                Placeholder(style.fontSize, style.fontSize, PlaceholderVerticalAlign.Bottom)
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = it,
                    ),
                    contentDescription = null,
                    modifier = modifier,
                    alignment = Alignment.Center
                )

            }
        ),
        onTextLayout = { layoutResult.value = it }
    )
}

