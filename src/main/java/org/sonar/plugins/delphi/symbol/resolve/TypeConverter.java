package org.sonar.plugins.delphi.symbol.resolve;

import static org.sonar.plugins.delphi.symbol.resolve.EqualityType.INCOMPATIBLE_TYPES;

import java.util.Set;
import org.sonar.plugins.delphi.type.Type;
import org.sonar.plugins.delphi.type.Type.StructType;

public final class TypeConverter {
  private TypeConverter() {
    // Utility class
  }

  public static TypeConversion convert(Type from, Type to) {
    Type fromConversionType = null;
    EqualityType fromConversionEquality = INCOMPATIBLE_TYPES;
    if (from.isStruct()) {
      Set<Type> implicit = ((StructType) from).typesWithImplicitConversionsFromThis();
      for (Type implicitConversion : implicit) {
        EqualityType convertEquality = TypeComparer.compare(implicitConversion, to);
        if (convertEquality.ordinal() > fromConversionEquality.ordinal()) {
          fromConversionEquality = convertEquality;
          fromConversionType = implicitConversion;
        }
      }
    }

    Type toConversionType = null;
    EqualityType toConversionEquality = INCOMPATIBLE_TYPES;
    if (to.isStruct()) {
      Set<Type> implicit = ((StructType) to).typesWithImplicitConversionsToThis();
      for (Type implicitConversion : implicit) {
        EqualityType convertEquality = TypeComparer.compare(from, implicitConversion);
        if (convertEquality.ordinal() > toConversionEquality.ordinal()) {
          toConversionEquality = convertEquality;
          toConversionType = implicitConversion;
        }
      }
    }

    EqualityType equality = INCOMPATIBLE_TYPES;
    if (fromConversionEquality.ordinal() > toConversionEquality.ordinal()) {
      equality = fromConversionEquality;
      from = fromConversionType;
    } else if (toConversionEquality != INCOMPATIBLE_TYPES) {
      equality = toConversionEquality;
      to = toConversionType;
    }

    return new TypeConversion(from, to, equality);
  }

  public static class TypeConversion {
    private final Type from;
    private final Type to;
    private final EqualityType equality;

    private TypeConversion(Type from, Type to, EqualityType equality) {
      this.from = from;
      this.to = to;
      this.equality = equality;
    }

    public Type getFrom() {
      return from;
    }

    public Type getTo() {
      return to;
    }

    public EqualityType getEquality() {
      return equality;
    }

    public boolean isSuccessful() {
      return equality != INCOMPATIBLE_TYPES;
    }
  }
}