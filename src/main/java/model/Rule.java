package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import parser.AlternativeParser;

public class Rule {

    private String name;
    private String body;
    private List<Rule> parents = new ArrayList<>();
    private Map<String, List<Rule>> alternatives = new HashMap<>();
    private List<Rule> children = new ArrayList<>();
    private List<String> alternativeBodies;

    public Rule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBody(String body){
        this.body = body;
        this.alternativeBodies = null;
    }

    public String getBody() {
        return body;
    }

    public List<String> getAlternativeBodies() {
        if (alternativeBodies == null) {
            alternativeBodies = AlternativeParser.split(body);
        }
        return Collections.unmodifiableList(alternativeBodies);
    }

    public void addChild(Rule child) {
        children.add(child);
        child.parents.add(this);
    }

    public List<Rule> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<Rule> getParents() {
        return  Collections.unmodifiableList(parents);
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rule)) {
            return false;
        }
        Rule rule = (Rule) o;
        return Objects.equals(name, rule.name)
                && Objects.equals(body, rule.body)
                && Objects.equals(parents, rule.parents)
                && Objects.equals(children, rule.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body, parents, children);
    }
}
