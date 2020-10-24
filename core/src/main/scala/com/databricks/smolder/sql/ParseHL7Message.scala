/*
 * Copyright 2020 Databricks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.databricks.smolder.sql

import com.databricks.smolder.Message
import org.apache.spark.sql.catalyst.expressions.codegen._
import org.apache.spark.sql.catalyst.expressions.{Expression, UnaryExpression}
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String

private[smolder] case class ParseHL7Message(child: Expression)
    extends UnaryExpression {

  override def dataType: DataType = Message.schema
  override def nullSafeEval(input: Any): Any = Message(input.asInstanceOf[UTF8String]).toInternalRow()

  override def doGenCode(ctx: CodegenContext, ev: ExprCode): ExprCode = {
    nullSafeCodeGen(
      ctx,
      ev,
      c => {
        s"""
         |${ev.value} =
         |com.databricks.smolder.Message.apply($c).toInternalRow();
       """.stripMargin
      }
    )
  }
}
