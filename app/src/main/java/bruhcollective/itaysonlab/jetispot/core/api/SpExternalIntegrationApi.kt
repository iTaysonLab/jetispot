package bruhcollective.itaysonlab.jetispot.core.api

import bruhcollective.itaysonlab.jetispot.core.objs.external.PersonalizedRecommendationsRequest
import bruhcollective.itaysonlab.jetispot.core.objs.external.PersonalizedRecommendationsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface SpExternalIntegrationApi {
    @POST("v2/personalized-recommendations")
    suspend fun personalizedRecommendations(
        @Body body: PersonalizedRecommendationsRequest
    ): PersonalizedRecommendationsResponse
}