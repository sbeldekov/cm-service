package com.example.cmservice.backend.test.config.context.company

import com.example.cmservice.generated.client.api.CompanyApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(
    name = "CompanyApiClient",
    url = "\${local.server.url}"
)
interface CompanyApiClient : CompanyApi
