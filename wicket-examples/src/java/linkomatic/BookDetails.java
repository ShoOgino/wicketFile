///////////////////////////////////////////////////////////////////////////////////
//
// Created May 21, 2004
//
// Copyright 2004, Jonathan W. Locke
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package linkomatic;

import com.voicetribe.wicket.markup.html.HtmlPage;
import com.voicetribe.wicket.markup.html.basic.Label;

/**
 * A book details page.  Shows information about a book.
 * @author Jonathan Locke
 */
public final class BookDetails extends HtmlPage
{
    /**
     * Constructor
     * @param book The model
     */
    public BookDetails(final Book book)
    {
        add(new Label("title", book, "title"));
    }
}

///////////////////////////////// End of File /////////////////////////////////
