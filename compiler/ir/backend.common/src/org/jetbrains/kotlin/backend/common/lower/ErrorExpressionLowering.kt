/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.lower

import org.jetbrains.kotlin.backend.common.CommonBackendContext
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrErrorDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCompositeImpl
import org.jetbrains.kotlin.ir.types.IrErrorType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

abstract class ErrorExpressionLowering(context: CommonBackendContext) : FileLoweringPass {

    protected val nothingType = context.irBuiltIns.nothingType

    abstract fun transformErrorExpression(expression: IrExpression, nodeString: String): IrExpression
    abstract fun transformErrorDeclaration(declaration: IrErrorDeclaration): IrDeclaration

    override fun lower(irFile: IrFile) {
        irFile.transformChildrenVoid(object : IrElementTransformerVoid() {

            override fun visitErrorExpression(expression: IrErrorExpression): IrExpression {
                return transformErrorExpression(expression, "Error Expression")
            }

            override fun visitErrorCallExpression(expression: IrErrorCallExpression): IrExpression {
                expression.transformChildrenVoid(this)
                val statements = mutableListOf<IrExpression>().apply {
                    expression.explicitReceiver?.let { add(it) }
                    addAll(expression.arguments)
                    add(transformErrorExpression(expression, "Error Call"))
                }
                return expression.run {
                    IrCompositeImpl(startOffset, endOffset, nothingType, null, statements)
                }
            }

            override fun visitErrorDeclaration(declaration: IrErrorDeclaration): IrStatement {
                return transformErrorDeclaration(declaration)
            }

            override fun visitTypeOperator(expression: IrTypeOperatorCall): IrExpression {
                expression.transformChildrenVoid(this)
                if (expression.typeOperand is IrErrorType) {
                    return expression.run {
                        IrCompositeImpl(startOffset, endOffset, nothingType, null, listOf(
                            argument, transformErrorExpression(this, "Error Type")
                        ))
                    }
                }

                return expression
            }
        })
    }
}