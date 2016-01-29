package org.json;

import com.mlizhi.C0111R;
import com.mlizhi.modules.spec.setting.SpecSettingMineActivity;
import com.tencent.connect.share.QQShare;
import java.util.HashMap;
import p016u.aly.dv;

public class XMLTokener extends JSONTokener {
    public static final HashMap entity;

    static {
        entity = new HashMap(8);
        entity.put("amp", XML.AMP);
        entity.put("apos", XML.APOS);
        entity.put("gt", XML.GT);
        entity.put("lt", XML.LT);
        entity.put("quot", XML.QUOT);
    }

    public XMLTokener(String s) {
        super(s);
    }

    public String nextCDATA() throws JSONException {
        StringBuffer sb = new StringBuffer();
        while (true) {
            char c = next();
            if (end()) {
                break;
            }
            sb.append(c);
            int i = sb.length() - 3;
            if (i >= 0 && sb.charAt(i) == ']' && sb.charAt(i + 1) == ']' && sb.charAt(i + 2) == '>') {
                sb.setLength(i);
                return sb.toString();
            }
        }
        throw syntaxError("Unclosed CDATA");
    }

    public Object nextContent() throws JSONException {
        char c;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        if (c == '\u0000') {
            return null;
        }
        if (c == '<') {
            return XML.LT;
        }
        StringBuffer sb = new StringBuffer();
        while (c != '<' && c != '\u0000') {
            if (c == '&') {
                sb.append(nextEntity(c));
            } else {
                sb.append(c);
            }
            c = next();
        }
        back();
        return sb.toString().trim();
    }

    public Object nextEntity(char ampersand) throws JSONException {
        StringBuffer sb = new StringBuffer();
        while (true) {
            char c = next();
            if (!Character.isLetterOrDigit(c) && c != '#') {
                break;
            }
            sb.append(Character.toLowerCase(c));
        }
        if (c == ';') {
            String string = sb.toString();
            Object object = entity.get(string);
            return object != null ? object : new StringBuffer().append(ampersand).append(string).append(";").toString();
        } else {
            throw syntaxError(new StringBuffer().append("Missing ';' in XML entity: &").append(sb).toString());
        }
    }

    public Object nextMeta() throws JSONException {
        char c;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        switch (c) {
            case dv.f2154a /*0*/:
                throw syntaxError("Misshaped meta tag");
            case SpecSettingMineActivity.REQUEST_CODE_ADDRESS /*33*/:
                return XML.BANG;
            case SpecSettingMineActivity.REQUEST_CODE_SEX /*34*/:
            case C0111R.styleable.View_filterTouchesWhenObscured /*39*/:
                char q = c;
                do {
                    c = next();
                    if (c == '\u0000') {
                        throw syntaxError("Unterminated string");
                    }
                } while (c != q);
                return Boolean.TRUE;
            case C0111R.styleable.View_contentDescription /*47*/:
                return XML.SLASH;
            case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                return XML.LT;
            case C0111R.styleable.View_layerType /*61*/:
                return XML.EQ;
            case C0111R.styleable.View_layoutDirection /*62*/:
                return XML.GT;
            case C0111R.styleable.View_textDirection /*63*/:
                return XML.QUEST;
        }
        while (true) {
            c = next();
            if (Character.isWhitespace(c)) {
                return Boolean.TRUE;
            }
            switch (c) {
                case dv.f2154a /*0*/:
                case SpecSettingMineActivity.REQUEST_CODE_ADDRESS /*33*/:
                case SpecSettingMineActivity.REQUEST_CODE_SEX /*34*/:
                case C0111R.styleable.View_filterTouchesWhenObscured /*39*/:
                case C0111R.styleable.View_contentDescription /*47*/:
                case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                case C0111R.styleable.View_layerType /*61*/:
                case C0111R.styleable.View_layoutDirection /*62*/:
                case C0111R.styleable.View_textDirection /*63*/:
                    back();
                    return Boolean.TRUE;
                default:
            }
        }
    }

    public Object nextToken() throws JSONException {
        char c;
        do {
            c = next();
        } while (Character.isWhitespace(c));
        StringBuffer sb;
        switch (c) {
            case dv.f2154a /*0*/:
                throw syntaxError("Misshaped element");
            case SpecSettingMineActivity.REQUEST_CODE_ADDRESS /*33*/:
                return XML.BANG;
            case SpecSettingMineActivity.REQUEST_CODE_SEX /*34*/:
            case C0111R.styleable.View_filterTouchesWhenObscured /*39*/:
                char q = c;
                sb = new StringBuffer();
                while (true) {
                    c = next();
                    if (c == '\u0000') {
                        throw syntaxError("Unterminated string");
                    } else if (c == q) {
                        return sb.toString();
                    } else {
                        if (c == '&') {
                            sb.append(nextEntity(c));
                        } else {
                            sb.append(c);
                        }
                    }
                }
            case C0111R.styleable.View_contentDescription /*47*/:
                return XML.SLASH;
            case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                throw syntaxError("Misplaced '<'");
            case C0111R.styleable.View_layerType /*61*/:
                return XML.EQ;
            case C0111R.styleable.View_layoutDirection /*62*/:
                return XML.GT;
            case C0111R.styleable.View_textDirection /*63*/:
                return XML.QUEST;
            default:
                sb = new StringBuffer();
                while (true) {
                    sb.append(c);
                    c = next();
                    if (Character.isWhitespace(c)) {
                        return sb.toString();
                    }
                    switch (c) {
                        case dv.f2154a /*0*/:
                            return sb.toString();
                        case SpecSettingMineActivity.REQUEST_CODE_ADDRESS /*33*/:
                        case C0111R.styleable.View_contentDescription /*47*/:
                        case C0111R.styleable.View_layerType /*61*/:
                        case C0111R.styleable.View_layoutDirection /*62*/:
                        case C0111R.styleable.View_textDirection /*63*/:
                        case '[':
                        case ']':
                            back();
                            return sb.toString();
                        case SpecSettingMineActivity.REQUEST_CODE_SEX /*34*/:
                        case C0111R.styleable.View_filterTouchesWhenObscured /*39*/:
                        case QQShare.QQ_SHARE_SUMMARY_MAX_LENGTH /*60*/:
                            throw syntaxError("Bad character in a name");
                        default:
                    }
                }
        }
    }

    public boolean skipPast(String to) throws JSONException {
        int i;
        int offset = 0;
        int length = to.length();
        char[] circle = new char[length];
        for (i = 0; i < length; i++) {
            char c = next();
            if (c == '\u0000') {
                return false;
            }
            circle[i] = c;
        }
        while (true) {
            int j = offset;
            boolean b = true;
            for (i = 0; i < length; i++) {
                if (circle[j] != to.charAt(i)) {
                    b = false;
                    break;
                }
                j++;
                if (j >= length) {
                    j -= length;
                }
            }
            if (b) {
                return true;
            }
            c = next();
            if (c == '\u0000') {
                return false;
            }
            circle[offset] = c;
            offset++;
            if (offset >= length) {
                offset -= length;
            }
        }
    }
}
