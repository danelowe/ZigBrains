/*
 * This file is part of ZigBrains.
 *
 * Copyright (C) 2023-2025 FalsePattern
 * All Rights Reserved
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * ZigBrains is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, only version 3 of the License.
 *
 * ZigBrains is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ZigBrains. If not, see <https://www.gnu.org/licenses/>.
 */

package com.falsepattern.zigbrains.zon.pairing

import com.falsepattern.zigbrains.zon.psi.ZonTypes
import com.intellij.codeInsight.editorActions.MultiCharQuoteHandler
import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import com.intellij.openapi.editor.highlighter.HighlighterIterator

class ZonQuoteHandler: SimpleTokenSetQuoteHandler(ZonTypes.STRING_LITERAL_SINGLE, ZonTypes.IDENTIFIER, ZonTypes.BAD_DQUOT, ZonTypes.CHAR_LITERAL, ZonTypes.BAD_SQUOT), MultiCharQuoteHandler {
    override fun getClosingQuote(iterator: HighlighterIterator, offset: Int) = when(iterator.tokenType) {
        ZonTypes.STRING_LITERAL_SINGLE, ZonTypes.IDENTIFIER, ZonTypes.BAD_DQUOT -> "\""
        ZonTypes.CHAR_LITERAL, ZonTypes.BAD_SQUOT -> "'"
        else -> null
    }
}