package com.troystopera.gencode.generator.statements

import com.troystopera.gencode.generator.*
import com.troystopera.jkode.components.CodeBlock

internal abstract class StatementProvider(type: ProviderType) : CodeProvider(type) {

    abstract fun populate(parent: CodeBlock, scope: GenScope, context: GenContext)

}