/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicket.util.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to a resource
 * @author Jonathan Locke
 */
public interface IResourceStream
{ // TODO finalize javadoc
    /**
     * @return Returns the inputStream.
     * @throws ResourceNotFoundException
     */
    public InputStream getInputStream() throws ResourceNotFoundException;

    /**
     * Closes input stream
     * @throws IOException
     */
    public void close() throws IOException;
}

///////////////////////////////// End of File /////////////////////////////////
