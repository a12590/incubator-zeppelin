/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.rest.message;

/**
 * Request message for creating new paragraph
 */
public class NewParagraphRequest {
  private String title;
  private String content;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}

//package org.apache.zeppelin.rest.message;
//
///**
// * NewParagraphRequest rest api request message
// *
// * index field will be ignored when it's used to provide initial paragraphs
// */
//public class NewParagraphRequest {
//  String title;
//  String text;
//  Double index;
//
//  public NewParagraphRequest() {
//
//  }
//
//  public String getTitle() {
//    return title;
//  }
//
//  public String getText() {
//    return text;
//  }
//
//  public Double getIndex() {
//    return index;
//  }
//>>>>>>> 11d25be8c3d13f55763609a4ccb93394771a6971
//}
