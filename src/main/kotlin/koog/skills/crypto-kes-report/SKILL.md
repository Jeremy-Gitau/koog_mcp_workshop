---
name: crypto-kes-report
description: Use when answering questions about cryptocurrency prices, market overviews, or trending coins, to format the response as a structured price report with a KES conversion instead of a plain paragraph.
---

Format any answer about coin prices, market overview, or trending coins as a short structured report.

1. Get the requested data using the available crypto tools (get_coin_price, get_trending_coins, get_market_overview).
2. If a USD price is returned, show the KES equivalent too, using whatever conversion the tool result already provides. If no KES figure is available from the tool, state the USD figure plainly and do not guess a rate.
3. Present each coin in this shape:

   COIN: <name and symbol>
   PRICE: <price in USD, and KES if available>
   24H CHANGE: <percentage, with a plus or minus sign>
   NOTE: <one short line of context, for example whether it is trending right now>

4. For multiple coins (trending coins, market overview), repeat the block per coin, most relevant first, then add one closing sentence that sums up the overall picture.
5. Keep the whole answer under ten lines. Do not add investment advice or disclaimers unless the user directly asks for advice rather than data.
