/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.protocol.ws.api;

import org.apache.wicket.Component;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.protocol.ws.api.event.WebSocketBinaryPayload;
import org.apache.wicket.protocol.ws.api.event.WebSocketClosedPayload;
import org.apache.wicket.protocol.ws.api.event.WebSocketConnectedPayload;
import org.apache.wicket.protocol.ws.api.event.WebSocketPayload;
import org.apache.wicket.protocol.ws.api.event.WebSocketTextPayload;
import org.apache.wicket.protocol.ws.api.message.BinaryMessage;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.message.TextMessage;

/**
 * A behavior that provides optional callbacks for the WebSocket
 * messages (connect, message, close)
 *
 * @since 6.0
 */
public abstract class WebSocketBehavior extends BaseWebSocketBehavior
{
	public WebSocketBehavior()
	{
	}

	@Override
	public void onEvent(Component component, IEvent<?> event)
	{
		super.onEvent(component, event);

		Object payload = event.getPayload();
		if (payload instanceof WebSocketPayload)
		{
			WebSocketPayload<?> wsPayload = (WebSocketPayload<?>) payload;
			WebSocketRequestHandler webSocketHandler = wsPayload.getHandler();

			if (payload instanceof WebSocketTextPayload)
			{
				WebSocketTextPayload textPayload = (WebSocketTextPayload) payload;
				TextMessage data = textPayload.getMessage();
				onMessage(webSocketHandler, data);
			}
			else if (wsPayload instanceof WebSocketBinaryPayload)
			{
				WebSocketBinaryPayload binaryPayload = (WebSocketBinaryPayload) wsPayload;
				BinaryMessage binaryData = binaryPayload.getMessage();
				onMessage(webSocketHandler, binaryData);
			}
			else if (wsPayload instanceof WebSocketConnectedPayload)
			{
				WebSocketConnectedPayload connectedPayload = (WebSocketConnectedPayload) wsPayload;
				ConnectedMessage message = connectedPayload.getMessage();
				onConnect(message);
			}
			else if (wsPayload instanceof WebSocketClosedPayload)
			{
				WebSocketClosedPayload connectedPayload = (WebSocketClosedPayload) wsPayload;
				ClosedMessage message = connectedPayload.getMessage();
				onClose(message);
			}
		}
	}

	protected void onConnect(ConnectedMessage message)
	{
	}

	protected void onClose(ClosedMessage message)
	{
	}

	protected void onMessage(WebSocketRequestHandler handler, TextMessage message)
	{
	}

	protected void onMessage(WebSocketRequestHandler handler, BinaryMessage binaryMessage)
	{
	}
}
