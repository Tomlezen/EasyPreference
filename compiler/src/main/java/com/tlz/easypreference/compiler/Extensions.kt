package com.tlz.easypreference.compiler

import com.squareup.kotlinpoet.*
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.Elements
import javax.lang.model.util.SimpleTypeVisitor6
import javax.lang.model.util.SimpleTypeVisitor7
import kotlin.reflect.full.primaryConstructor

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 15:39.
 */
fun TypeElement.pkg(elementUtils: Elements) = elementUtils.getPackageOf(this).qualifiedName.toString()

fun TypeName.isFloat() = this == FLOAT
fun TypeName.isString() = this == String::class.asTypeName()

fun TypeMirror.typeName(): TypeName {
  val name = toString()
  return when (name) {
    "java.lang.Object" -> Any::class.asTypeName()
    "java.lang.String" -> String::class.asTypeName()
    "boolean", "java.lang.Boolean" -> Boolean::class.asTypeName()
    "byte", "java.lang.Byte" -> Byte::class.asTypeName()
    "char", "java.lang.Character" -> Char::class.asTypeName()
    "short", "java.lang.Short" -> Short::class.asTypeName()
    "int", "java.lang.Integer" -> Int::class.asTypeName()
    "long", "java.lang.Long" -> Long::class.asTypeName()
    "float", "java.lang.Float" -> Float::class.asTypeName()
    "double", "java.lang.Double" -> Double::class.asTypeName()
    "java.lang.CharSequence" -> CharSequence::class.asTypeName()
    else -> {
      if (name.startsWith("java.util.List") || name.startsWith("java.util.ArrayList")) {
        val declaredType = this.accept(object : SimpleTypeVisitor6<DeclaredType, String>() {
          override fun visitDeclared(t: DeclaredType?, p: String?): DeclaredType = t!!
        }, this.toString())
        val typeArguments = declaredType.typeArguments
        if (typeArguments.isEmpty()) {
          return this.asTypeName()
        }
        return asCustomTypeName()
      } else {
        return asTypeName()
      }
    }
  }
}

fun ClassName.check() =
    when (canonicalName) {
      "java.lang.Object" -> Any::class.asTypeName()
      "java.lang.String" -> String::class.asTypeName()
      "boolean", "java.lang.Boolean" -> Boolean::class.asTypeName()
      "byte", "java.lang.Byte" -> Byte::class.asTypeName()
      "char", "java.lang.Character" -> Char::class.asTypeName()
      "short", "java.lang.Short" -> Short::class.asTypeName()
      "int", "java.lang.Integer" -> Int::class.asTypeName()
      "long", "java.lang.Long" -> Long::class.asTypeName()
      "float", "java.lang.Float" -> Float::class.asTypeName()
      "double", "java.lang.Double" -> Double::class.asTypeName()
      "java.lang.CharSequence" -> CharSequence::class.asTypeName()
      else -> this
    }

fun TypeMirror.asCustomTypeName(): TypeName {
  return this.accept(object : SimpleTypeVisitor7<TypeName, Void?>() {
    override fun visitPrimitive(t: PrimitiveType, p: Void?) =
        when (t.kind) {
          TypeKind.BOOLEAN -> BOOLEAN
          TypeKind.BYTE -> BYTE
          TypeKind.SHORT -> SHORT
          TypeKind.INT -> INT
          TypeKind.LONG -> LONG
          TypeKind.CHAR -> CHAR
          TypeKind.FLOAT -> FLOAT
          TypeKind.DOUBLE -> DOUBLE
          else -> throw AssertionError()
        }

    override fun visitDeclared(t: DeclaredType, p: Void?): TypeName {
      val rawType: ClassName = (t.asElement() as TypeElement).asCustomClassName()
      val enclosingType = t.enclosingType
      val enclosing = if (enclosingType.kind != TypeKind.NONE && !t.asElement().modifiers.contains(
          Modifier.STATIC)) enclosingType.accept(this, null) else null
      if (t.typeArguments.isEmpty() && enclosing !is ParameterizedTypeName) {
        return rawType.check()
      }

      val typeArgumentNames = mutableListOf<TypeName>()
      for (typeArgument in t.typeArguments) {
        typeArgumentNames += typeArgument.asCustomTypeName()
      }
      val constructor = ParameterizedTypeName::class.primaryConstructor
      return (enclosing as? ParameterizedTypeName)?.nestedClass(rawType.simpleName(),
          typeArgumentNames) ?: constructor?.call(null, rawType, typeArgumentNames, false,
          listOf<AnnotationSpec>())!!
    }

    override fun visitError(t: ErrorType, p: Void?): TypeName = visitDeclared(t, p)

    override fun visitArray(t: ArrayType, p: Void?): ParameterizedTypeName =
        ParameterizedTypeName.get(ARRAY, t.componentType.asTypeName())

    override fun visitTypeVariable(t: TypeVariable, p: Void?): TypeName = t.asCustomTypeName()

    override fun visitWildcard(t: WildcardType, p: Void?): TypeName = t.asWildcardTypeName()

    override fun visitNoType(t: NoType, p: Void?): TypeName {
      if (t.kind == TypeKind.VOID) return UNIT
      return super.visitUnknown(t, p)
    }

    override fun defaultAction(e: TypeMirror?, p: Void?): TypeName {
      throw IllegalArgumentException("Unexpected type mirror: " + e!!)
    }
  }, null)
}

private fun isClassOrInterface(e: Element): Boolean = e.kind.isClass || e.kind.isInterface

private fun getPackage(type: Element): PackageElement {
  var t = type
  while (t.kind != ElementKind.PACKAGE) {
    t = t.enclosingElement
  }
  return t as PackageElement
}

fun TypeElement.asCustomClassName(): ClassName {
  val names = mutableListOf<String>()
  var e: Element = this
  while (isClassOrInterface(e)) {
    val eType = e as TypeElement
    require(eType.nestingKind == NestingKind.TOP_LEVEL || eType.nestingKind == NestingKind.MEMBER) {
      "unexpected type testing"
    }
    names += eType.simpleName.toString()
    e = eType.enclosingElement
  }
  val name = getPackage(this).qualifiedName.toString()
  names += if (name.startsWith("java.util")) "kotlin.collections" else name
  names.reverse()
  return ClassName::class.primaryConstructor?.call(names, false, listOf<AnnotationSpec>())!!
}