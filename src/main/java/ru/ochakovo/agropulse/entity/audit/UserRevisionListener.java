package ru.ochakovo.agropulse.entity.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserRevisionEntity userRevision = (UserRevisionEntity) revisionEntity;
        userRevision.setUsername(username);
    }
}
