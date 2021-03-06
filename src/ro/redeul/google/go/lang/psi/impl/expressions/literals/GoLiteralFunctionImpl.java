package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

public class GoLiteralFunctionImpl extends GoPsiElementBase implements GoLiteralFunction {

    public GoLiteralFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isInit() { return false; }

    @NotNull
    @Override
    public GoFunctionDeclaration getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return Type.Function;
    }

    @Override
    public String getFunctionName() {
        return null;
    }

    @Override
    public boolean isMain() {
        return false;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    @Override
    public GoFunctionParameter[] getParameters() {
        return GoPsiUtils.getParameters(this);
    }

    @Override
    public GoFunctionParameter[] getResults() {
        PsiElement result = findChildByType(GoElementTypes.FUNCTION_RESULT);

        return GoPsiUtils.getParameters(result);
    }

    @NotNull
    @Override
    public GoType[] getParameterTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getParameters());
    }

    @NotNull
    @Override
    public GoType[] getReturnTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getResults());
    }

    @Override
    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public boolean isVariadic() {
        GoFunctionParameter parameters[] = getParameters();
        return parameters.length > 0 && parameters[parameters.length - 1].isVariadic();
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionLiteral(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        for (GoFunctionParameter functionParameter : getParameters()) {
            if (!processor.execute(functionParameter, state)) {
                return false;
            }
        }
        for (GoFunctionParameter functionParameter : getResults()) {
            if (!processor.execute(functionParameter, state)) {
                return false;
            }
        }

        return processor.execute(this, state);
    }
}
