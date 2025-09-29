package com.devsu.banking.person_customer.model.commons;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class PageDTO<T> {

  private List<T> content;
  private PageMetadata pageMetadata;

}
