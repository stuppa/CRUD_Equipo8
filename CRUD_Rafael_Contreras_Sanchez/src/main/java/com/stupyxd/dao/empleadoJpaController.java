/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stupyxd.dao;

import com.odry.dao.exceptions.NonexistentEntityException;
import com.odry.dao.exceptions.PreexistingEntityException;
import com.odry.dominio.empleado;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author sheir
 */
public class empleadoJpaController implements Serializable {
    private EntityManagerFactory emf = null;
    
    public empleadoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("myDB");
    }
    

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(empleado empleado) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(empleado);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findempleado(empleado.getId()) != null) {
                throw new PreexistingEntityException("empleado " + empleado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(empleado empleado) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            empleado = em.merge(empleado);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = empleado.getId();
                if (findempleado(id) == null) {
                    throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            empleado empleado;
            try {
                empleado = em.getReference(empleado.class, id);
                empleado.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The empleado with id " + id + " no longer exists.", enfe);
            }
            em.remove(empleado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<empleado> buscarTodos() {
        return findempleadoEntities(true, -1, -1);
    }

    public List<empleado> findempleadoEntities(int maxResults, int firstResult) {
        return findempleadoEntities(false, maxResults, firstResult);
    }

    private List<empleado> findempleadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(empleado.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public empleado findempleado(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(empleado.class, id);
        } finally {
            em.close();
        }
    }

    public int getempleadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<empleado> rt = cq.from(empleado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public void cerrar(){
        emf.close();
    }
    
}
