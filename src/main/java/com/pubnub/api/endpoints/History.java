package com.pubnub.api.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.pubnub.api.core.PnResponse;
import com.pubnub.api.core.Pubnub;
import com.pubnub.api.core.PubnubException;
import com.pubnub.api.core.models.HistoryData;
import com.pubnub.api.core.models.HistoryItemData;
import lombok.Builder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import java.util.HashMap;
import java.util.Map;

@Builder
public class History extends Endpoint<JsonNode, HistoryData> {

    private Pubnub pubnub;
    private String channel;
    private long start = -1;
    private long end = -1;
    private boolean reverse = false;
    private int count = -1;
    private boolean includeTimetoken = false;

    interface HistoryService {
        @GET("v2/history/sub-key/{subKey}/channel/{channel}")
        Call<JsonNode> fetchHistory(@Path("subKey") String subKey, @Path("channel") String channel, @QueryMap Map<String, String> options);
    }

    @Override
    protected boolean validateParams() {
        return true;
    }

    @Override
    protected Call<JsonNode> doWork() {
        Map<String, String> params = new HashMap<String, String>();

        HistoryService service = this.createRetrofit(pubnub).create(HistoryService.class);

        params.put("reverse", String.valueOf(reverse));
        params.put("include_token", String.valueOf(includeTimetoken));

        if (count > 0 && count <= 10){
            params.put("count", String.valueOf(count));
        } else {
            params.put("count", "100");
        }

        if (start != -1) {
            params.put("start", Long.toString(start).toLowerCase());
        }
        if (end != -1) {
            params.put("end", Long.toString(end).toLowerCase());
        }

        return service.fetchHistory(pubnub.getConfiguration().getSubscribeKey(), channel, params);
    }

    @Override
    protected PnResponse<HistoryData> createResponse(Response<JsonNode> input) throws PubnubException {
        PnResponse<HistoryData> pnResponse = new PnResponse<HistoryData>();
        pnResponse.fillFromRetrofit(input);

        if (input.body() != null){
            HistoryData historyData = new HistoryData();
            historyData.setStartTimeToken(input.body().get(1).asLong());
            historyData.setEndTimeToken(input.body().get(2).asLong());

            ArrayNode historyItems = (ArrayNode) input.body().get(0);

            for (final JsonNode historyItem : historyItems) {
                HistoryItemData historyItemData = new HistoryItemData();
                Object message;

                if (includeTimetoken){
                    historyItemData.setTimetoken(historyItem.get("timetoken").asLong());
                    message = historyItem.get("message");
                } else {
                    message = historyItem;
                }

                historyItemData.setEntry(message);
                historyData.getItems().add(historyItemData);
            }

            pnResponse.setPayload(historyData);
        }

        return pnResponse;
    }
}